package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
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
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.challenge.ChallengeRequestDTO;
import site.challenger.project_challenger.service.ChallengeService;
import site.challenger.project_challenger.util.InsuUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenge")
public class ChallengeController {
	private final ChallengeService challengeService;

	// 챌린지 추가
	@PostMapping("/add")
	public CommonResponseDTO addNewChallenge(Authentication authentication, HttpServletRequest request,
			@RequestBody ChallengeRequestDTO challengeRequestDTO) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		CommonResponseDTO reponse = challengeService.addNewChallenge(requestUserNo, challengeRequestDTO);

		return reponse;
	}

	// 챌린지 가져오기
	@GetMapping
	public CommonResponseDTO viewAllChallengeByLocationRef(Authentication authentication, HttpServletRequest request,
			@RequestParam(required = false) long locationRef, @RequestParam(required = false) long targetUserNo) {

		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		// 가능한 시나리오
		// 1. 특정 유저가 구독중인 챌린지를 가져옴 ()
		// 2. 어떤 지역의 챌린지를 가져옴 (인기순)
		// 3. 자신이 구독중인 챌린지를 가져옴 ()
		// 4. 추천 알고리즘에 따라 챌린지를 보여줌
		// 5. 검색어에 따라 챌린지를 가져옴 (인기순)

		// 시나리오 1
		CommonResponseDTO response = challengeService.getAllSubcribedChallengeByUserNo(targetUserNo, requestUserNo);

		return response;
	}

	// 챌린지 좋아요 하기/취소
	@GetMapping("/recommend/{chNo}")
	public CommonResponseDTO recommendChallenge(Authentication authentication, HttpServletRequest request,
			@PathVariable long chNo) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		CommonResponseDTO response = challengeService.recommendChallenge(requestUserNo, chNo);

		return response;
	}

//	 딜리트 기능 보류 10명이하면 삭제됨 현재
	@DeleteMapping("/delete/{chNO}")
	public CommonResponseDTO deleteChallenge(Authentication authentication, HttpServletRequest request,
			@PathVariable long chNo) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		CommonResponseDTO response = challengeService.deleteChallenge(requestUserNo, chNo);

		return response;
	}

	// 챌린지 구독 하기/취소 보류 (# 자신이 만든 챌린지는 구독 취소가 불가능하게)
	@GetMapping("/subscribe/{chNo}")
	public CommonResponseDTO subscribeChallenge(Authentication authentication, HttpServletRequest request,
			@PathVariable long chNo) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		CommonResponseDTO response = challengeService.subscribeChallenge(requestUserNo, chNo);

		return response;
	}

}
