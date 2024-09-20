package site.challenger.project_challenger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.JwtModel;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeRequestDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeResponseDTO;
import site.challenger.project_challenger.service.ChallengeService;
import site.challenger.project_challenger.util.JwtParser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge")
public class ChallengeController {
	private final JwtParser jwtParser;
	private final ChallengeService challengeService;

	// 챌린지 추가
	@PostMapping("/add")
	public ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>> addNewChallenge(HttpServletRequest request,
			@RequestBody ChallengeRequestDTO challengeRequestDTO) {
		JwtModel jwtModel = jwtParser.fromCookie(request);

		challengeRequestDTO.setJwtModel(jwtModel);

		CommonResponseDTO<ChallengeResponseDTO> responseDto = challengeService.addNewChallenge(challengeRequestDTO);

		return new ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>>(responseDto, responseDto.getHttpStatus());
	}

	// 챌린지 가져오기
	@GetMapping
	public ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>> viewAllChallengeByLocationRef(
			HttpServletRequest request, @RequestParam(required = false) long locationRef,
			@RequestParam(required = false) long userNo) {
		// 지역별

		// 유저별
		CommonResponseDTO<ChallengeResponseDTO> reponseDto = challengeService.getAllChallengeByUserNo(userNo);

		return new ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>>(reponseDto, reponseDto.getHttpStatus());
	}

	// 챌린지 좋아요 하기/취소
	@GetMapping("/recommend/{chNo}")
	public ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>> recommendChallenge(HttpServletRequest request,
			@PathVariable long chNo) {
		JwtModel jwtModel = jwtParser.fromCookie(request);

		CommonResponseDTO<ChallengeResponseDTO> responseDto = challengeService.recommendChallenge(jwtModel.getNo(),
				chNo);

		return new ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>>(responseDto, responseDto.getHttpStatus());
	}

//	 딜리트 기능 보류 10명이하면 삭제됨 현재
	@DeleteMapping("/delete/{chNO}")
	public ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>> deleteChallenge(HttpServletRequest request,
			@PathVariable long chNo) {
		JwtModel jwtModel = jwtParser.fromCookie(request);

		CommonResponseDTO<ChallengeResponseDTO> responseDto = challengeService.deleteChallenge(jwtModel.getNo(), chNo);

		return new ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>>(responseDto, responseDto.getHttpStatus());
	}

	// 챌린지 구독 하기/취소
	@GetMapping("/subscribe/{chNo}")
	public ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>> subscribeChallenge(HttpServletRequest request,
			@PathVariable long chNo) {

		JwtModel jwtModel = jwtParser.fromCookie(request);

		CommonResponseDTO<ChallengeResponseDTO> responseDto = challengeService.subscribeChallenge(jwtModel.getNo(),
				chNo);

		return new ResponseEntity<CommonResponseDTO<ChallengeResponseDTO>>(responseDto, responseDto.getHttpStatus());
	}

}
