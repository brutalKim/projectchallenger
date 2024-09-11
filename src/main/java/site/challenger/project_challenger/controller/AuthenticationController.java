package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.authentication.SignupDTO;
import site.challenger.project_challenger.dto.authentication.SignupDTO.SignupReqDTO;
import site.challenger.project_challenger.dto.authentication.SignupDTO.SignupResDTO;
import site.challenger.project_challenger.dto.authentication.SignupDTO.SignupServiceReqDTO;
import site.challenger.project_challenger.service.UserManegementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {
	private final UserManegementService userManagementService;
	private final SignupDTO signupDTO;
	@PostMapping("/signup")
	public SignupResDTO signup(Authentication authentication, @RequestBody SignupReqDTO req) {
		String uid = authentication.getName();
		String nickname = req.getNickname();
		String opt1 = req.getLocationOpt1();
		String opt2 = req.getLocationOpt2();
		SignupServiceReqDTO signupServiceReqDTO = signupDTO.new SignupServiceReqDTO(uid,nickname,opt1,opt2);
		return userManagementService.saveUser(signupServiceReqDTO);
	}
}
