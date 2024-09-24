package site.challenger.project_challenger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
	// 가능한 시나리오
	// 1. 특정 유저가 구독중인 챌린지를 가져옴 / 자신이 구독중인 챌린지를 가져옴 (추천 많이받은 순) target user
	// 2. 어떤 지역의 챌린지를 가져옴 (추천 많이받은 순) target location
	// 3. 검색어에 따라 챌린지를 가져옴 (추천 많이받은 순) target keyword
	// 4. 특정 챌린지 하나를 가져옴 target challenge
	// 5. 추천 알고리즘에 따라 챌린지를 보여줌 target recommend

	@GetMapping
	public CommonResponseDTO viewAllChallengeByLocationRef(Authentication authentication, HttpServletRequest request,
			@RequestParam(required = true) String target,
			//
			@RequestParam(required = false, defaultValue = "0") long targetNo,
			//
			@RequestParam(required = false) String keyWord,
			//
			@RequestParam(required = false, defaultValue = "0") int page) {

		// page사이즈 10으로 고정할거임

		CommonResponseDTO response;

		// 시나리오 1
		if (target.equals("user")) {
			if (targetNo == 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "targetNo을 받지못함");
			}
			response = challengeService.getAllSubcribedChallengeByUserNo(targetNo, page);
			return response;
		}
		// 시나리오 2
		else if (target.equals("location")) {
			if (targetNo == 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "targetNo을 받지못함");
			}
			response = challengeService.getAllChallengeByLocationRefNo(targetNo, page);
			return response;
		}
		// 시나리오 3
		else if (target.equals("keyword")) {
			if (keyWord == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "키워드를 받지못함");
			}
			response = challengeService.getAllChallengeByKeyWord(keyWord, page);
			return response;
		}
		// 시나리오 4
		else if (target.equals("challenge")) {
			if (targetNo == 0) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "targetNo을 받지못함");
			}
			response = challengeService.getChallengeByChallengeNumber(targetNo,
					InsuUtils.getRequestUserNo(authentication));
			return response;
		}
		// 시나리오 5
		else if (target.equals("recommend")) {
			return null;
		}
		return null;

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
	@DeleteMapping("/delete/{chNo}")
	public CommonResponseDTO deleteChallenge(Authentication authentication, HttpServletRequest request,
			@PathVariable long chNo) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		CommonResponseDTO response = challengeService.deleteChallenge(requestUserNo, chNo);

		return response;
	}

	// 챌린지 구독 하기/취소 보류
	@GetMapping("/subscribe/{chNo}")
	public CommonResponseDTO subscribeChallenge(Authentication authentication, HttpServletRequest request,
			@PathVariable long chNo) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		CommonResponseDTO response = challengeService.subscribeChallenge(requestUserNo, chNo);

		return response;
	}

}
