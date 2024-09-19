package site.challenger.project_challenger.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	public CommonResponseDTO<ChallengeResponseDTO> addNewChallenge(ChallengeRequestDTO challengeRequestDTO) {
		// challenge 저장,
		LocationRef locationRef = locationRefRepository.findByOpt1AndOpt2(challengeRequestDTO.getLocationOpt1(),
				challengeRequestDTO.getLocationOpt2());

		Users user = getUserByUserNo(challengeRequestDTO.getJwtModel().getNo());

		Challenge challenge = new Challenge(user, locationRef, challengeRequestDTO.getTitle(),
				challengeRequestDTO.getContent());

		challenge = challengeRepository.save(challenge);

		ChallengeResponseDTO challengeResponseDTO = null;

		CommonResponseDTO<ChallengeResponseDTO> reponseDto = null;

		List<ChallengeResponseDTO> list = new ArrayList<>();
		if (null != challenge) {
			// 성공

			challengeResponseDTO = ChallengeResponseDTO.builder().title(challenge.getTitle())
					.content(challenge.getContent()).no(challenge.getNo()).recommend(challenge.getRecommend())
					.locationRef(challenge.getLocationRef()).recommended(false).build();

			list.add(challengeResponseDTO);

			reponseDto = new CommonResponseDTO<ChallengeResponseDTO>(HttpStatus.CREATED, "success",
					"view new challenge", true, list);
		} else {
			// 실패

			reponseDto = new CommonResponseDTO<ChallengeResponseDTO>(HttpStatus.INTERNAL_SERVER_ERROR,
					"failed to add new Challenge", "show error", false, list);

		}

		return reponseDto;

	}

	public CommonResponseDTO<ChallengeResponseDTO> getAllChallengeByUserNo(long userNo) {

		Users user = getUserByUserNo(userNo);

		List<Challenge> challenges = challengeRepository.findByUsers(user);

		List<ChallengeResponseDTO> responseList = new ArrayList<>();

		for (Challenge challenge : challenges) {
			boolean recommended = isUserRecommendedChallenge(user, challenge);

			ChallengeResponseDTO responseDto = ChallengeResponseDTO.builder().no(challenge.getNo())
					.title(challenge.getTitle()).content(challenge.getContent()).locationRef(challenge.getLocationRef())
					.recommend(challenge.getRecommend()).recommended(recommended).build();

			responseList.add(responseDto);
		}

		CommonResponseDTO<ChallengeResponseDTO> reponse = new CommonResponseDTO<ChallengeResponseDTO>(HttpStatus.OK,
				"성공적으로 가져옴", "", true, responseList);

		return reponse;
	}

	@Transactional
	public CommonResponseDTO<ChallengeResponseDTO> recommendChallenge(long userNo, long chNo) {

		Users userWhoIsToRecommend = getUserByUserNo(userNo);

		Challenge challenge = getChallengeByChNo(chNo);

		List<ChallengeResponseDTO> responseList = new ArrayList<>();

		boolean recommended = isUserRecommendedChallenge(userWhoIsToRecommend, challenge);
		boolean subscribed = isUserSubscribedChallenge(userWhoIsToRecommend, challenge);

		ChallengeRecommendPrimaryKey challengeRecommendPrimaryKey = new ChallengeRecommendPrimaryKey(
				userWhoIsToRecommend, challenge);

		// 자추금지
//		if (challenge.getUsers().getNo() == userWhoIsToRecommend.getNo()) {
//			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't recommend yourself");
//		}

		if (!recommended) {
			// 추천안했음
			challenge.incrementRecommend();
			ChallengeRecommend challengeRecommend = new ChallengeRecommend(challengeRecommendPrimaryKey,
					userWhoIsToRecommend, challenge, LocalDateTime.now());
			challengeRecommendRepository.save(challengeRecommend);
			challengeRepository.save(challenge);

		} else {
			// 추천 이미했음
			challenge.decrementRecommend();
			ChallengeRecommend challengeRecommend = challengeRecommendRepository.findById(challengeRecommendPrimaryKey)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
							"There's no such recommend record"));
			challengeRecommendRepository.delete(challengeRecommend);
			challengeRepository.save(challenge);

		}

		ChallengeResponseDTO responseDto = new ChallengeResponseDTO();
		responseDto.setContent(challenge.getContent());
		responseDto.setLocationRef(challenge.getLocationRef());
		responseDto.setNo(challenge.getNo());
		responseDto.setRecommend(challenge.getRecommend());
		responseDto.setRecommended(!recommended);
		responseDto.setSubscribed(subscribed);
		responseDto.setTitle(challenge.getTitle());
		if (subscribed) {
			ChallengeSub challengeSub = challengeSubRepository.findByUsersAndChallenge(userWhoIsToRecommend, challenge)
					.get();
			responseDto.setSubDateTime(challengeSub.getDate());
		}

		responseList.add(responseDto);

		return new CommonResponseDTO<ChallengeResponseDTO>(HttpStatus.OK, recommended ? "추천 취소" : "추천 성공", "", true,
				responseList);

	}

	public CommonResponseDTO<ChallengeResponseDTO> deleteChallenge(long userNo, long chNo) {

		Users user = getUserByUserNo(userNo);

		Challenge challenge = getChallengeByChNo(chNo);

		if (user.getNo() != challenge.getUsers().getNo()) {
			// 지우려는 사람이 만든 사람하고 다르면
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't delete other's Ch");
		}

		long countOfChallengeSub = challengeSubRepository.countByChallenge(challenge);
		if (countOfChallengeSub < 10) {
			challengeRepository.delete(challenge);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "10명 이상 구독중인 챌린지는 삭제할 수 없습니다.");
		}

		ChallengeResponseDTO challengeResponseDTO = ChallengeResponseDTO.builder().build();
		List<ChallengeResponseDTO> responseList = new ArrayList<>();
		responseList.add(challengeResponseDTO);

		CommonResponseDTO<ChallengeResponseDTO> responseDto = new CommonResponseDTO<ChallengeResponseDTO>(HttpStatus.OK,
				"성공적으로 지움", "", true, responseList);

		return responseDto;
	}

	public CommonResponseDTO<ChallengeResponseDTO> subscribeChallenge(long userNo, long chNo) {
		Users user = getUserByUserNo(userNo);
		Challenge challenge = getChallengeByChNo(chNo);

		boolean subscribed = isUserSubscribedChallenge(user, challenge);

		boolean recommended = isUserRecommendedChallenge(user, challenge);

		List<ChallengeResponseDTO> responseList = new ArrayList<>();
		ChallengeResponseDTO challengeResponseDTO = ChallengeResponseDTO.builder().title(challenge.getTitle())
				.content(challenge.getContent()).locationRef(challenge.getLocationRef()).no(challenge.getNo())
				.recommend(challenge.getRecommend()).recommended(recommended).build();

		if (subscribed) {
			// 이미 구독중이면
			challengeSubRepository.delete(challengeSubRepository.findByUsersAndChallenge(user, challenge).get());
			challengeResponseDTO.setSubscribed(!subscribed);
		} else {
			// 구독중이 아니면
			ChallengeSub newChallengeSub = new ChallengeSub(user, challenge);
			challengeSubRepository.save(newChallengeSub);
			challengeResponseDTO.setSubscribed(!subscribed);
			challengeResponseDTO.setSubDateTime(LocalDateTime.now());
		}

		CommonResponseDTO<ChallengeResponseDTO> reponseDto = new CommonResponseDTO<ChallengeResponseDTO>(HttpStatus.OK,
				subscribed ? "구독 취소" : "구독 성공", "", true, responseList);

		return reponseDto;
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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There's no such User"));
	}

	// 챌린지 넘버로 챌린지 가져오기
	private Challenge getChallengeByChNo(long chNo) {

		return challengeRepository.findById(chNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There's no such Ch"));
	}

}
