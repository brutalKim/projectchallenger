package site.challenger.project_challenger.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.ChallengeRecommend;
import site.challenger.project_challenger.domain.ChallengeRecommendPrimaryKey;
import site.challenger.project_challenger.domain.ChallengeSub;
import site.challenger.project_challenger.domain.LocationRef;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeRequestDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeResponseDTO;
import site.challenger.project_challenger.repository.ChallengeRecommendRepository;
import site.challenger.project_challenger.repository.ChallengeRepository;
import site.challenger.project_challenger.repository.ChallengeSubRepository;
import site.challenger.project_challenger.repository.LocationRefRepository;
import site.challenger.project_challenger.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChallengeService {
	private final LocationRefRepository locationRefRepository;
	private final ChallengeRepository challengeRepository;
	private final UserRepository userRepository;
	private final ChallengeRecommendRepository challengeRecommendRepository;
	private final ChallengeSubRepository challengeSubRepository;

	@Transactional
	public CommonResponseDTO addNewChallenge(long requestUserNo, ChallengeRequestDTO challengeRequestDTO) {
		// challenge 저장,
		LocationRef locationRef = locationRefRepository.findByOpt1AndOpt2(challengeRequestDTO.getLocationOpt1(),
				challengeRequestDTO.getLocationOpt2());

		Users user = getUserByUserNo(requestUserNo);

		Challenge challenge = new Challenge(user, locationRef, challengeRequestDTO.getTitle(),
				challengeRequestDTO.getContent());

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
			challengeResponseDTO = getDtofillWithChallenge(challenge);
			challengeResponseDTO.setRecommended(false);
			challengeResponseDTO.setSubDateTime(challengeSub.getDate());
			challengeResponseDTO.setSubscribed(isUserSubscribedChallenge(user, challenge));

			responseList.add(challengeResponseDTO);

			response = new CommonResponseDTO(body, HttpStatus.CREATED, "성공적으로 챌린지를 생성함", "챌린지 보여줄 것", true);
		} else {
			// 실패
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "챌린지를 생성하지 못함");
		}
		return response;
	}

	public CommonResponseDTO getAllSubcribedChallengeByUserNo(long targetUserNo, long requestUserNo) {

		// 해당 유저가 구독중인!! 모든 챌린지를 가져옴

		Users targetUser = getUserByUserNo(targetUserNo);
		Users requestUser = getUserByUserNo(requestUserNo);

		List<ChallengeSub> challengeSubs = challengeSubRepository.findByUser(targetUser);
		List<Challenge> challenges = new ArrayList<>();

		for (ChallengeSub challengeSub : challengeSubs) {
			challenges.add(challengeRepository.findById(challengeSub.getNo())
					.orElseThrow(() -> throwNewResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"해당 챌린지가 존재하지 않음 불러오는 도중에 삭제됨")));
		}

		Map<String, Object> body = new HashMap<>();
		List<ChallengeResponseDTO> responseList = new ArrayList<>();
		body.put("responseList", responseList);

		for (Challenge challenge : challenges) {
			boolean recommended = isUserRecommendedChallenge(requestUser, challenge);

			ChallengeResponseDTO responseDto = getDtofillWithChallenge(challenge);

			responseDto.setRecommended(recommended);

			boolean subscribed = isUserSubscribedChallenge(requestUser, challenge);
			ChallengeSub challengeSub;
			if (subscribed) {
				challengeSub = challengeSubRepository.findByUsersAndChallenge(requestUser, challenge).get();
				responseDto.setSubscribed(subscribed);
				responseDto.setSubDateTime(challengeSub.getDate());
			}

			responseList.add(responseDto);
		}

		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK, "성공적으로 가져옴", "챌린지들 보여줄 것", true);

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
					.orElseThrow(() -> throwNewResponseStatusException("There's no such recommend record"));
			challengeRecommendRepository.delete(challengeRecommend);
			challenge = challengeRepository.save(challenge);

		}

		ChallengeResponseDTO responseDto = getDtofillWithChallenge(challenge);
		responseDto.setRecommended(!recommended);
		responseDto.setSubscribed(subscribed);
		if (subscribed) {
			ChallengeSub challengeSub = challengeSubRepository.findByUsersAndChallenge(requestUser, challenge).get();
			responseDto.setSubDateTime(challengeSub.getDate());
		}

		responseList.add(responseDto);

		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK, recommended ? "추천 취소" : "추천 성공",
				"추천 숫자 변경", true);

		return response;

	}

	// 빈 응답으로 나중에 수정 요망
	public CommonResponseDTO deleteChallenge(long requestUserNo, long chNo) {

		Users requestUser = getUserByUserNo(requestUserNo);

		Challenge targetChallenge = getChallengeByChNo(chNo);

		if (requestUser.getNo() != targetChallenge.getUsers().getNo()) {
			// 지우려는 사람이 만든 사람하고 다르면 삭제 불가
			throw throwNewResponseStatusException(HttpStatus.FORBIDDEN, "다른 사람의 챌린지는 삭제할 수 없습니다.");
		}

		long countOfChallengeSub = challengeSubRepository.countByChallenge(targetChallenge);

		if (countOfChallengeSub < 10) {
			challengeRepository.delete(targetChallenge);
		} else {
			throw throwNewResponseStatusException(HttpStatus.FORBIDDEN, "10명 이상 구독중인 챌린지는 삭제할 수 없습니다.");
		}

		CommonResponseDTO response = new CommonResponseDTO(null, HttpStatus.OK, "성공적으로 삭제됨", "챌린지 삭제됨 다른곳으로", true);

		return response;
	}

	public CommonResponseDTO subscribeChallenge(long requestUserNo, long chNo) {

		Users requestUser = getUserByUserNo(requestUserNo);
		Challenge challenge = getChallengeByChNo(chNo);

		boolean subscribed = isUserSubscribedChallenge(requestUser, challenge);
		boolean recommended = isUserRecommendedChallenge(requestUser, challenge);

		List<ChallengeResponseDTO> responseList = new ArrayList<>();
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("responseList", responseList);

		ChallengeResponseDTO challengeResponseDTO = getDtofillWithChallenge(challenge);
		challengeResponseDTO.setRecommended(recommended);

		if (subscribed) {
			// 이미 구독중이면
			ChallengeSub challengeSub = challengeSubRepository.findByUsersAndChallenge(requestUser, challenge).get();
//			if(challengeSub.getUsers().getNo() == requestUser.getNo() ) {
//				// 챌린지 생성자와 챌린지 구독 취소자가 같으면 구독취소 안해줌
//				throw throwNewResponseStatusException(HttpStatus.FORBIDDEN, "자신이 생성한 챌린지는 구독을 취소할 수 없습니다")
//			}
			challengeSubRepository.delete(challengeSub);
			challengeResponseDTO.setSubscribed(!subscribed);
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

	// 이 유저가 이 챌린지 추천을 했는가
	private boolean isUserRecommendedChallenge(Users user, Challenge challenge) {
		ChallengeRecommendPrimaryKey challengeRecommendPrimaryKey = new ChallengeRecommendPrimaryKey(user, challenge);

		return challengeRecommendRepository.existsById(challengeRecommendPrimaryKey);
	}

	// 이 유저가 이 챌린치를 구독을 했는가
	private boolean isUserSubscribedChallenge(Users user, Challenge challenge) {

		return challengeSubRepository.existsByUsersAndChallenge(user, challenge);
	}

	// 유저넘버로 유저 가져오기
	private Users getUserByUserNo(long userNo) {

		return userRepository.findById(userNo)
				.orElseThrow(() -> throwNewResponseStatusException("There's no such User"));
	}

	// 챌린지 넘버로 챌린지 가져오기
	private Challenge getChallengeByChNo(long chNo) {

		return challengeRepository.findById(chNo)
				.orElseThrow(() -> throwNewResponseStatusException("There's no such Ch"));
	}

	// sub관련 2개 , 추천 관련 1개 수동으로 넣어줘야함
	private ChallengeResponseDTO getDtofillWithChallenge(Challenge challenge) {
		ChallengeResponseDTO responseDTO = new ChallengeResponseDTO();
		responseDTO.setTitle(challenge.getTitle());
		responseDTO.setContent(challenge.getContent());
		responseDTO.setLocationRef(challenge.getLocationRef());
		responseDTO.setNo(challenge.getNo());
		responseDTO.setRecommend(challenge.getRecommend());

		return responseDTO;
	}

	private ResponseStatusException throwNewResponseStatusException(HttpStatus httpStatus, String reason) {

		return new ResponseStatusException(httpStatus, reason);
	}

	private ResponseStatusException throwNewResponseStatusException(String reason) {

		return new ResponseStatusException(HttpStatus.NOT_FOUND, reason);
	}

}
