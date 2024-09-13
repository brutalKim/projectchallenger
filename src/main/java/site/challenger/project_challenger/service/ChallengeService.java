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
import site.challenger.project_challenger.domain.LocationRef;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeRequestDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeResponseDTO;
import site.challenger.project_challenger.repository.ChallengeRecommendRepository;
import site.challenger.project_challenger.repository.ChallengeRepository;
import site.challenger.project_challenger.repository.LocationRefRepository;
import site.challenger.project_challenger.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChallengeService {
	private final LocationRefRepository locationRefRepository;
	private final ChallengeRepository challengeRepository;
	private final UserRepository userRepository;
	private final ChallengeRecommendRepository challengeRecommendRepository;

	public CommonResponseDTO<ChallengeResponseDTO> addNewChallenge(ChallengeRequestDTO challengeRequestDTO) {
		// challenge 저장,
		LocationRef locationRef = locationRefRepository.findByOpt1AndOpt2(challengeRequestDTO.getLocationOpt1(),
				challengeRequestDTO.getLocationOpt2());
		System.out.println(locationRef);

		Users user = userRepository.findById(challengeRequestDTO.getJwtModel().getNo()).get();

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
					.locationRef(challenge.getLocationRef()).build();

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

		Users user = userRepository.findById(userNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There's no such user"));

		List<Challenge> challenges = challengeRepository.findByUsers(user);

		List<ChallengeResponseDTO> responseList = new ArrayList<>();

		for (Challenge challenge : challenges) {
			ChallengeResponseDTO responseDto = ChallengeResponseDTO.builder().no(challenge.getNo())
					.title(challenge.getTitle()).content(challenge.getContent()).locationRef(challenge.getLocationRef())
					.recommend(challenge.getRecommend()).build();

			responseList.add(responseDto);
		}

		CommonResponseDTO<ChallengeResponseDTO> reponse = new CommonResponseDTO<ChallengeResponseDTO>(HttpStatus.OK, "",
				"", true, responseList);

		return reponse;
	}

	@Transactional
	public CommonResponseDTO<ChallengeResponseDTO> recommendChallenge(long userNo, long chNo, boolean isRecommend) {

		Users userWhoIsToRecommend = userRepository.findById(userNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There's no such user"));

		Challenge challenge = challengeRepository.findById(chNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Thers's no such Challenge"));

		ChallengeRecommendPrimaryKey challengeRecommendPrimaryKey = new ChallengeRecommendPrimaryKey(
				userWhoIsToRecommend, challenge);

		List<ChallengeResponseDTO> responseList = new ArrayList<>();

		// 자추금지
//		if (challenge.getUsers().getNo() == userWhoIsToRecommend.getNo()) {
//			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't recommend yourself");
//		}

		if (isRecommend) {
			// 추천
			challenge.incrementRecommend();
			ChallengeRecommend challengeRecommend = new ChallengeRecommend(challengeRecommendPrimaryKey,
					userWhoIsToRecommend, challenge, LocalDateTime.now());
			challengeRecommendRepository.save(challengeRecommend);
			challengeRepository.save(challenge);

		} else {
			// 추천 해제
			challenge.decrementRecommend();
			ChallengeRecommend challengeRecommend = challengeRecommendRepository.findById(challengeRecommendPrimaryKey)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
							"There's no such recommend record"));
			challengeRecommendRepository.delete(challengeRecommend);
			challengeRepository.save(challenge);

		}

		ChallengeResponseDTO responseDto = new ChallengeResponseDTO();
		responseDto.setRecommend(challenge.getRecommend());
		responseList.add(responseDto);

		return new CommonResponseDTO<ChallengeResponseDTO>(HttpStatus.OK, "", "", true, responseList);

	}

}
