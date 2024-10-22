package site.challenger.project_challenger.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.ChallengeHasPost;
import site.challenger.project_challenger.domain.ChallengeRecommend;
import site.challenger.project_challenger.domain.ChallengeRecommendPrimaryKey;
import site.challenger.project_challenger.domain.ChallengeSub;
import site.challenger.project_challenger.domain.LocationRef;
import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.domain.PostRecommendPrimaryKey;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeRequestDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeResponseDTO;
import site.challenger.project_challenger.dto.post.PostDTO;
import site.challenger.project_challenger.repository.ChallengeHasPostRepository;
import site.challenger.project_challenger.repository.ChallengeRecommendRepository;
import site.challenger.project_challenger.repository.ChallengeRepository;
import site.challenger.project_challenger.repository.ChallengeSubRepository;
import site.challenger.project_challenger.repository.LocationRefRepository;
import site.challenger.project_challenger.repository.PostCommentRepository;
import site.challenger.project_challenger.repository.PostRecommendRepository;
import site.challenger.project_challenger.repository.PostRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.util.InsuUtils;

@Service
@RequiredArgsConstructor
public class ChallengeService {
	private final LocationRefRepository locationRefRepository;
	private final ChallengeRepository challengeRepository;
	private final UserRepository userRepository;
	private final ChallengeRecommendRepository challengeRecommendRepository;
	private final ChallengeSubRepository challengeSubRepository;
	private final ChallengeHasPostRepository challengeHasPostRepository;
	private final PostRepository postRepository;
	private final PostCommentRepository postCommentRepository;
	private final PostRecommendRepository postRecommendRepository;

	@Transactional
	public CommonResponseDTO addNewChallenge(long requestUserNo, ChallengeRequestDTO challengeRequestDTO) {
		// challenge 저장,
		LocationRef locationRef = locationRefRepository.findByOpt1AndOpt2(challengeRequestDTO.getLocationOpt1(),
				challengeRequestDTO.getLocationOpt2());

		Users user = getUserByUserNo(requestUserNo);

		Challenge challenge = new Challenge();
		challenge.setUsers(user);
		challenge.setLocationRef(locationRef);
		challenge.setTitle(challengeRequestDTO.getTitle());
		challenge.setContent(challengeRequestDTO.getContent());
		challenge.setRecommend(0L);
		challenge.setAbled(true);

		challenge = challengeRepository.save(challenge);

		// 챌린지 생성자는 자동으로 챌린지 구독자가 됨
		ChallengeSub challengeSub = new ChallengeSub(user, challenge);
		challengeSubRepository.save(challengeSub);

		CommonResponseDTO response;

		List<ChallengeResponseDTO> responseList = new ArrayList<>();
		Map<String, Object> body = new HashMap<>();
		body.put("responseList", responseList);
		ChallengeResponseDTO challengeResponseDTO = null;

		if (null != challenge) {
			// 성공
			challengeResponseDTO = getDtofillWithChallenge(challenge, user);

			responseList.add(challengeResponseDTO);

			response = new CommonResponseDTO(body, HttpStatus.CREATED, "성공적으로 챌린지를 생성함", "챌린지 보여줄 것", true);
		} else {
			// 실패
			throw InsuUtils.throwNewResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "챌린지를 생성하지 못했음");
		}
		return response;
	}

	// 시나리오 1
	public CommonResponseDTO getAllSubcribedChallengeByUserNo(long targetUserNo, long requestUserNo, int page) {

		Pageable pageable = PageRequest.of(page, 10);

		// 해당 유저가 구독중인!! 모든 챌린지를 가져옴

		Users targetUser = getUserByUserNo(targetUserNo);

		Users requestUser = getUserByUserNo(requestUserNo);

		Page<ChallengeSub> pagedChallengeSubs = challengeSubRepository.findByUsersSortedByRecommend(targetUserNo,
				pageable);
		List<ChallengeSub> challengeSubs = pagedChallengeSubs.getContent();
		List<Challenge> challenges = new ArrayList<>();

		for (ChallengeSub challengeSub : challengeSubs) {
			challenges.add(challengeRepository.findActiveById(challengeSub.getChallenge().getNo())
					.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("해당 챌린지가 존재하지 않음 불러오는 도중에 삭제됨")));
		}

		Map<String, Object> body = new HashMap<>();
		List<ChallengeResponseDTO> responseList = new ArrayList<>();
		body.put("responseList", responseList);

		// 페이지 정보 넣기
		InsuUtils.insertMapWithPageInfo(body, pagedChallengeSubs);

		for (Challenge challenge : challenges) {
			ChallengeResponseDTO responseDto = getDtofillWithChallenge(challenge, requestUser);

			responseList.add(responseDto);
		}

		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK, "성공적으로 가져옴", "챌린지들 보여줄 것", true);

		return response;
	}

	// 시나리오 2
	public CommonResponseDTO getAllChallengeByLocationRefNo(long targetLocationRefNo, long requestUserNo, int page) {

		Users requestUser = getUserByUserNo(requestUserNo);

		Pageable pageable = PageRequest.of(page, 10, Sort.by("recommend").descending());

		LocationRef locationRef = locationRefRepository.findById(targetLocationRefNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException(HttpStatus.NOT_FOUND, "해당 로케이션이 존재하지 않음"));

		Page<Challenge> pagedChallenges = challengeRepository.findByLocationRefAndAbledTrue(locationRef, pageable);

		List<Challenge> challenges = pagedChallenges.getContent();

		Map<String, Object> body = new HashMap<String, Object>();
		List<ChallengeResponseDTO> responseList = new ArrayList<ChallengeResponseDTO>();
		body.put("responseList", responseList);

		// 페이지 정보 넣기
		InsuUtils.insertMapWithPageInfo(body, pagedChallenges);

		for (Challenge challenge : challenges) {
			responseList.add(getDtofillWithChallenge(challenge, requestUser));
		}
		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK);

		return response;
	}

	// 시나리오 3
	public CommonResponseDTO getAllChallengeByKeyWord(String keyword, long requestUserNo, int page) {

		Users requestUser = getUserByUserNo(requestUserNo);

		Pageable pageable = PageRequest.of(page, 10, Sort.by("recommend").descending());

		Page<Challenge> pagedChallenges = challengeRepository.findByTitleContainingAndAbledTrue(keyword, pageable);
		List<Challenge> challenges = pagedChallenges.getContent();

		Map<String, Object> body = new HashMap<String, Object>();
		List<ChallengeResponseDTO> responseList = new ArrayList<ChallengeResponseDTO>();
		body.put("responseList", responseList);

		InsuUtils.insertMapWithPageInfo(body, pagedChallenges);

		for (Challenge challenge : challenges) {
			responseList.add(getDtofillWithChallenge(challenge, requestUser));
		}
		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK);

		return response;
	}

	// 시나리오 4
	public CommonResponseDTO getChallengeByChallengeNumber(long chNo, long requestUserNo) {

		Users requestUser = getUserByUserNo(requestUserNo);
		Challenge challenge = getChallengeByChNo(chNo);

		Map<String, Object> body = new HashMap<String, Object>();
		List<ChallengeResponseDTO> responseList = new ArrayList<ChallengeResponseDTO>();

		ChallengeResponseDTO challengeResponseDTO = getDtofillWithChallenge(challenge, requestUser);

		responseList.add(challengeResponseDTO);
		body.put("responseList", responseList);

		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK);

		return response;
	}

	// 시나리오 5
	// 지역구 전체, 전국구
	// 추천순, 포스트순
	// (하루간 많은, 일주일간 많은)
	// 하나씩 하면 최대 8 개 -> 중복 없다고 가정했을 때 적당한 수치인듯
	// -- 2개씩 16개

	// 만들어야함
	public CommonResponseDTO genarateRecommendedChallenge(long requestUserNo, int page) {
		Set<Challenge> feed = new HashSet<>();
		List<ChallengeResponseDTO> responseList = new ArrayList<ChallengeResponseDTO>();
		Map<String, Object> body = new HashMap<String, Object>();

		Users requestUser = getUserByUserNo(requestUserNo);

		LocationRef userLocation = requestUser.getLocationRef();
		String targetLocationOpt1 = userLocation.getOpt1();

		LocalDateTime startDate = LocalDateTime.now().minusDays(1);
		Pageable pageable = PageRequest.of(page, 2);

		List<Challenge> case1 = challengeRepository.findMostRecommendedChallengesByOpt1FromStartDate(targetLocationOpt1,
				startDate, pageable);
		List<Challenge> case2 = challengeRepository.findMostRecommendedChallengesFromStartDate(startDate, pageable);
		List<Challenge> case3 = challengeRepository
				.findTopChallengesByPostCountInByOpt1FromStartDate(targetLocationOpt1, startDate, pageable);
		List<Challenge> case4 = challengeRepository.findTopChallengesByPostCountInFromStartDate(startDate, pageable);

		startDate = LocalDateTime.now().minusWeeks(1);

		List<Challenge> case5 = challengeRepository.findMostRecommendedChallengesByOpt1FromStartDate(targetLocationOpt1,
				startDate, pageable);
		List<Challenge> case6 = challengeRepository.findMostRecommendedChallengesFromStartDate(startDate, pageable);
		List<Challenge> case7 = challengeRepository
				.findTopChallengesByPostCountInByOpt1FromStartDate(targetLocationOpt1, startDate, pageable);
		List<Challenge> case8 = challengeRepository.findTopChallengesByPostCountInFromStartDate(startDate, pageable);

		feed.addAll(case1);
		feed.addAll(case2);
		feed.addAll(case3);
		feed.addAll(case4);
		feed.addAll(case5);
		feed.addAll(case6);
		feed.addAll(case7);
		feed.addAll(case8);

		for (Challenge challenge : feed) {
			ChallengeResponseDTO challengeResponseDTO = getDtofillWithChallenge(challenge, requestUser);
			responseList.add(challengeResponseDTO);
		}
		body.put("responseList", responseList);

		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK, null, null, true);
		return response;

	}

	@Transactional
	public CommonResponseDTO recommendChallenge(long requestUserNo, long chNo) {

		Users requestUser = getUserByUserNo(requestUserNo);

		Challenge challenge = getChallengeByChNo(chNo);

		Map<String, Object> body = new HashMap<String, Object>();
		List<ChallengeResponseDTO> responseList = new ArrayList<>();
		body.put("responseList", responseList);

		boolean recommended = isUserRecommendedChallenge(requestUser, challenge);
		boolean subscribed = isUserSubscribedChallenge(requestUser, challenge);

		ChallengeRecommendPrimaryKey challengeRecommendPrimaryKey = new ChallengeRecommendPrimaryKey(requestUser,
				challenge);

		if (!recommended) {
			// 추천안했음
			challenge.incrementRecommend();
			ChallengeRecommend challengeRecommend = new ChallengeRecommend(challengeRecommendPrimaryKey, requestUser,
					challenge, LocalDateTime.now());
			challengeRecommendRepository.save(challengeRecommend);
			challenge = challengeRepository.save(challenge);

		} else {
			// 추천 이미했음
			challenge.decrementRecommend();
			ChallengeRecommend challengeRecommend = challengeRecommendRepository.findById(challengeRecommendPrimaryKey)
					.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("추천 기록이 없음"));
			challengeRecommendRepository.delete(challengeRecommend);
			challenge = challengeRepository.save(challenge);

		}

		ChallengeResponseDTO responseDto = getDtofillWithChallenge(challenge, requestUser);

		responseList.add(responseDto);

		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK, recommended ? "추천 취소" : "추천 성공",
				"추천 숫자 변경", true);

		return response;

	}

	public CommonResponseDTO deleteChallenge(long requestUserNo, long chNo) {

		Users requestUser = getUserByUserNo(requestUserNo);

		Challenge targetChallenge = getChallengeByChNo(chNo);

		if (requestUser.getNo() != targetChallenge.getUsers().getNo()) {
			// 지우려는 사람이 만든 사람하고 다르면 삭제 불가
			throw InsuUtils.throwNewResponseStatusException(HttpStatus.FORBIDDEN, "다른 사람의 챌린지는 삭제할 수 없습니다.");
		}

		long countOfChallengeSub = challengeSubRepository.countByChallenge(targetChallenge);

		if (countOfChallengeSub < 10) {
			// 실제 딜리트가 아닌 -> unabled 로 바꾸기 !
			targetChallenge.setAbled(false);
			challengeRepository.save(targetChallenge);
		} else {
			throw InsuUtils.throwNewResponseStatusException(HttpStatus.FORBIDDEN, "10명 이상 구독중인 챌린지는 삭제할 수 없습니다.");
		}

		CommonResponseDTO response = new CommonResponseDTO(HttpStatus.OK, "성공적으로 삭제됨");

		return response;
	}

	@Transactional
	public CommonResponseDTO subscribeChallenge(long requestUserNo, long chNo) {

		Users requestUser = getUserByUserNo(requestUserNo);
		Challenge challenge = getChallengeByChNo(chNo);

		boolean subscribed = isUserSubscribedChallenge(requestUser, challenge);

		List<ChallengeResponseDTO> responseList = new ArrayList<>();
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("responseList", responseList);

		ChallengeResponseDTO challengeResponseDTO = getDtofillWithChallenge(challenge, requestUser);

		if (subscribed) {
			// 이미 구독중이면 취소함
			ChallengeSub challengeSub = challengeSubRepository.findByUsersAndChallengeAbledTrue(requestUser, challenge)
					.get();
			challengeSubRepository.delete(challengeSub);
			challengeResponseDTO.setSubscribed(!subscribed);

			// 구독 취소한 뒤에 구독자 0명인 챌린지는 지움 -> 실제로는 안지움
			long numOfChallenge = challengeSubRepository.countByChallenge(challenge);
			if (numOfChallenge == 0) {
				challenge.setAbled(false);
				challengeRepository.save(challenge);
				return new CommonResponseDTO(HttpStatus.OK, true);
			}
		} else {
			// 구독중이 아니면
			ChallengeSub newChallengeSub = new ChallengeSub(requestUser, challenge);
			challengeSubRepository.save(newChallengeSub);
			challengeResponseDTO.setSubscribed(!subscribed);
			challengeResponseDTO.setSubDateTime(LocalDateTime.now());
		}
		responseList.add(challengeResponseDTO);

		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK, subscribed ? "구독 취소" : "구독 성공",
				"성공 취소 ui업데이트", true);

		return response;
	}

	public CommonResponseDTO getPostsByChNo(long requestUserNo, long chNo, int page) {
		Users requestUser = getUserByUserNo(requestUserNo);
		// 인기순 있어도 괜찮을듯?

		// 최신순

		Pageable pageable = PageRequest.of(page, 10, Sort.by("date").descending());

		Page<Post> postByChallengeNo = postRepository.getPostByChallengeNo(chNo, pageable);

		Map<String, Object> body = new HashMap<String, Object>();
		List<PostDTO> responseList = new ArrayList<>();
		body.put("responseList", responseList);
		InsuUtils.insertMapWithPageInfo(body, postByChallengeNo);

		for (Post post : postByChallengeNo) {
			long postNo = post.getNo();
//			Post post = postRepository.findById(postNo)
//					.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("해당 포스트가 존재하지 않음"));
			Users postWriter = post.getUsers();

			PostDTO postDTO = new PostDTO(null, null, null, null, null, null);

			postDTO.setCommentCount(postCommentRepository.countByPostNo(postNo));
			postDTO.setContent(post.getContent());
			postDTO.setDate(post.getDate());

			postDTO.setImages(post.getPostImage().stream().map((item) -> item.getStoredName()).toList());
			postDTO.setNo(post.getNo());

			postDTO.setProfileImg(postWriter.getProfile().getSavedName());
			postDTO.setRecommend(post.getRecommend());

			boolean isUserRecommendPost = postRecommendRepository
					.existsById(new PostRecommendPrimaryKey(requestUser, post));
			postDTO.setRecommended(isUserRecommendPost);

			ArrayList<ChallengeHasPost> byPostNo = challengeHasPostRepository.findByPostNo(postNo);

			for (ChallengeHasPost challengeHasPost2 : byPostNo) {
				long challengeNo = challengeHasPost2.getChallengeHasPostPrimaryKey().getChallengeNo();
				Challenge challengeByChNo = getChallengeByChNo(challengeNo);
				postDTO.addTaggedChallenge(challengeByChNo.getTitle(), challengeNo);
			}

			postDTO.setUsersNo(postWriter.getNo());
			postDTO.setWriterNickname(postWriter.getNickname());

			responseList.add(postDTO);
		}
		// 최신순 정렬
//		responseList.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

		return new CommonResponseDTO(body, HttpStatus.OK);
	}

	// 이 유저가 이 챌린지 추천을 했는가
	private boolean isUserRecommendedChallenge(Users user, Challenge challenge) {
		ChallengeRecommendPrimaryKey challengeRecommendPrimaryKey = new ChallengeRecommendPrimaryKey(user, challenge);

		return challengeRecommendRepository.existsById(challengeRecommendPrimaryKey);
	}

	// 이 유저가 이 챌린치를 구독을 했는가
	private boolean isUserSubscribedChallenge(Users user, Challenge challenge) {

		return challengeSubRepository.existsByUsersAndChallengeAbledTrue(user, challenge);
	}

	private LocalDateTime whenUserSubscribedChallenge(Users user, Challenge challenge) {
		return challengeSubRepository.findByUsersAndChallengeAbledTrue(user, challenge)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("구독 기록이 없음")).getDate();
	}

	// 유저넘버로 유저 가져오기
	private Users getUserByUserNo(long userNo) {

		return userRepository.findById(userNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("There's no such User"));
	}

	// 챌린지 넘버로 챌린지 가져오기
	private Challenge getChallengeByChNo(long chNo) {

		return challengeRepository.findActiveById(chNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("There's no such Ch"));
	}

	// 챌린지로 챌린지 구독자 수 가져오기
	private long getFollwerByCh(Challenge challenge) {
		return challengeSubRepository.countByChallenge(challenge);

	}

	// 챌린지로 포스트 몇개나 있는지 가져오기
	private long getPostNumByCh(Challenge challenge) {
		List<ChallengeHasPost> list = challengeHasPostRepository.findByChallengeNo(challenge.getNo());
		if (list.isEmpty()) {
			return 0;
		}
		return list.size();
	}

	private ChallengeResponseDTO getDtofillWithChallenge(Challenge challenge, Users user) {
		ChallengeResponseDTO responseDTO = new ChallengeResponseDTO();
		responseDTO.setNo(challenge.getNo());
		responseDTO.setTitle(challenge.getTitle());
		responseDTO.setContent(challenge.getContent());
		responseDTO.setLocationRef(challenge.getLocationRef());
		responseDTO.setRecommend(challenge.getRecommend());
		responseDTO.setRecommended(isUserRecommendedChallenge(user, challenge));
		if (isUserSubscribedChallenge(user, challenge)) {
			responseDTO.setSubscribed(true);
			responseDTO.setSubDateTime(whenUserSubscribedChallenge(user, challenge));
		}
		responseDTO.setPostNum(getPostNumByCh(challenge));
		responseDTO.setFollower(getFollwerByCh(challenge));

		return responseDTO;
	}

}
