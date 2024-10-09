package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.service.LoginService;
import site.challenger.project_challenger.util.InsuUtils;

@RestController
@RequiredArgsConstructor
public class LoginController2 {

	private final LoginService loginService;

	@GetMapping("/afterSuccess")
	public CommonResponseDTO loginFailed2(Authentication authentication) {

		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		return loginService.afterLogin(requestUserNo);
	}
	@GetMapping("/getuser")
	public CommonResponseDTO getuser(Authentication authentication ,@RequestParam(required = false) Long userNo) {
		if(userNo == null) {
			long requestUserNo = InsuUtils.getRequestUserNo(authentication);
			return loginService.afterLogin(requestUserNo);
		}
		return loginService.afterLogin(userNo);
	}

}
