package site.challenger.project_challenger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.JwtModel;
import site.challenger.project_challenger.dto.challenge.ChallengeRequestDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeResponseDTO;
import site.challenger.project_challenger.service.ChallengeService;
import site.challenger.project_challenger.util.JwtParser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge")
public class ChallengeController2 {
	private final JwtParser jwtParser;
	private final ChallengeService challengeService;

	@PostMapping("/add")
	public ResponseEntity<ChallengeResponseDTO> addNewChallenge(HttpServletRequest request,
			@RequestBody ChallengeRequestDTO challengeRequestDTO) {

		System.out.println(challengeRequestDTO);
		JwtModel jwtModel = jwtParser.fromHeader(request);

		challengeRequestDTO.setJwtModel(jwtModel);

		ChallengeResponseDTO challengeResponseDTO = challengeService.addNewChallenge(challengeRequestDTO);

		return new ResponseEntity<ChallengeResponseDTO>(challengeResponseDTO, challengeResponseDTO.getHttpStatus());
	}

}
