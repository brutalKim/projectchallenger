package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.service.ChallengeLogService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class ChallengeLogController {
	private final ChallengeLogService challengeLogService;
	//챌린지 로그 기록
	@PostMapping
	public CommonResponseDTO createLog(Authentication authentication, @RequestParam Long challengeNo) {
		Long userNo = Long.parseLong(authentication.getName());
		return challengeLogService.createLog(userNo, challengeNo);
	}
	//챌린지 기록 조회
	@GetMapping
	public CommonResponseDTO getLog(Authentication authentication, @RequestParam Long challengeNo) {
		Long userNo = Long.parseLong(authentication.getName());
		return challengeLogService.getLog(userNo, challengeNo);
	}
	//구독중인 챌린지 조회
	@GetMapping("/sub")
	public CommonResponseDTO getLogDeatil(Authentication authentication, @RequestParam Long userNo) {
		return challengeLogService.getSub(userNo);
	}
	//년도별 챌린지 로그
	@GetMapping("/year")
	public CommonResponseDTO getLogYear(@RequestParam Long subNum,@RequestParam int year) {
		return challengeLogService.getLogByYear(subNum,year);
	}
}
