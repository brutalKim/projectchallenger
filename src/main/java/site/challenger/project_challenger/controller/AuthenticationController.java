package site.challenger.project_challenger.controller;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.authentication.SignupReqDTO;
import site.challenger.project_challenger.dto.authentication.SignupResDTO;
import site.challenger.project_challenger.dto.authentication.SignupServiceReqDTO;
import site.challenger.project_challenger.filter.JwtTokenValidatorFilter;
import site.challenger.project_challenger.security.JwtProvider;
import site.challenger.project_challenger.service.UserManegementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {
	private final UserManegementService userManagementService;
	private final JwtDecoder jwtDecoder;
	private final JwtProvider jwtProovider;
	private final JwtTokenValidatorFilter jwtFilter;
	//회원가입
	@PostMapping("/signup")
	public SignupResDTO signup(Authentication authentication, @RequestBody SignupReqDTO req,HttpServletRequest request,HttpServletResponse response) {
		
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String cookieToken = jwtFilter.resolveTokenFromCookies(request);
		//헤더를 통한 인증도 허용함
		String oauthRefString = null;
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}
		//쿠키를 이용한 회원가입 진행 테스트
		if (cookieToken != null) {
			token = cookieToken;
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
		

		SignupResDTO resDTO = userManagementService.saveUser(signupServiceReqDTO);
		if(resDTO.isStatus()) {
			Cookie jwtCookie = new Cookie("JWT_TOKEN", resDTO.getMsg());
			jwtCookie.setHttpOnly(true);
			jwtCookie.setSecure(true);
			jwtCookie.setPath("/");
//			jwtCookie.setMaxAge(120);// 설정안하면 세션쿠키됨 
			response.addCookie(jwtCookie);
			return resDTO;
		}
		return resDTO;
	}
	//로그인 최초 접속시 회원정보
	@GetMapping("/login")
	public String login(Authentication authentication) {
		Long userId = Long.parseLong(authentication.getName());
		return userManagementService.login(userId);
	}
}