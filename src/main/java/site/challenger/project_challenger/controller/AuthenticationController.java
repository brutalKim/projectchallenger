package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.authentication.SignupReqDTO;
import site.challenger.project_challenger.dto.authentication.SignupResDTO;
import site.challenger.project_challenger.dto.authentication.SignupServiceReqDTO;
import site.challenger.project_challenger.security.JwtProvider;
import site.challenger.project_challenger.service.UserManegementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {
	private final UserManegementService userManagementService;
	private final JwtDecoder jwtDecoder;
	private final JwtProvider jwtProovider;
	@PostMapping("/signup")
	public SignupResDTO signup(Authentication authentication, @RequestBody SignupReqDTO req,HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String oauthRefString = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		try {
			Jwt jwt = jwtDecoder.decode(token);
			oauthRefString = jwt.getClaimAsString("oauthRef");
		}catch (Exception e) {
			e.printStackTrace();
		}
		String uid = authentication.getName();
		String nickname = req.getNickname();
		String opt1 = req.getLocationOpt1();
		String opt2 = req.getLocationOpt2();
		int oauthRef = Integer.parseInt(oauthRefString);
		SignupServiceReqDTO signupServiceReqDTO = new SignupServiceReqDTO(uid,nickname,opt1,opt2,oauthRef);
		return userManagementService.saveUser(signupServiceReqDTO);
	}
}