package site.challenger.project_challenger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.service.ChallengeService;

@RestController
@RequiredArgsConstructor
public class LoginController2 {
	private final ChallengeService challengeService;

//	@GetMapping("/1")
//	public CommonResponseDTO loginSuccess(HttpSession session, Authentication authentication) {
//		System.out.println(authentication);
//
//		return challengeService.getAllChallengeByLocationRefNo(1, 0);
//
//	}

	@GetMapping("/3")
	public String loginFailed2() {
		return "로그인 성공ㅇㅇㅇㅇㅇ";
	}

}
