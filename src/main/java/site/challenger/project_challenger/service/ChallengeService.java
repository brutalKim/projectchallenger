package site.challenger.project_challenger.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.LocationRef;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.challenge.ChallengeRequestDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeResponseDTO;
import site.challenger.project_challenger.repository.ChallengeRepository;
import site.challenger.project_challenger.repository.LocationRefRepository;
import site.challenger.project_challenger.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ChallengeService {
	private final LocationRefRepository locationRefRepository;
	private final ChallengeRepository challengeRepository;
	private final UserRepository userRepository;

	public ChallengeResponseDTO addNewChallenge(ChallengeRequestDTO challengeRequestDTO) {
		// challenge 저장,
		LocationRef locationRef = locationRefRepository.findByOpt1AndOpt2(challengeRequestDTO.getLocationOpt1(),
				challengeRequestDTO.getLocationOpt2());
		System.out.println(locationRef);

		Users user = userRepository.findById(challengeRequestDTO.getJwtModel().getNo()).get();

		Challenge challenge = new Challenge(user, locationRef, challengeRequestDTO.getTitle());

		challenge = challengeRepository.save(challenge);

		ChallengeResponseDTO challengeResponseDTO = null;

		if (null != challenge) {
			// 성공
			challengeResponseDTO = new ChallengeResponseDTO(HttpStatus.CREATED, "success", "view new challenge",
					challenge.getNo(), challenge.getTitle(), locationRef, 0);
		} else {
			// 실패
			challengeResponseDTO = new ChallengeResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR,
					"failed to add new Challenge", "");
		}

		return challengeResponseDTO;

	}

}
