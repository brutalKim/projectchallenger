package site.challenger.project_challenger.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// <<<<<<< insu0909-1
// =======
// import jakarta.servlet.http.HttpSession;
// import lombok.Builder;
// import lombok.Getter;
// >>>>>>> main
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.constants.MyRole;
import site.challenger.project_challenger.constants.SecurityConstants;
import site.challenger.project_challenger.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private static Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);
	private final JwtEncoder jwtEncoder;
	private final UserRepository userRepository;
	private final JwtProvider jwtProvider;

// Oauth2 인증을 하면 얘가 처리함 정보->
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String uid = authentication.getName();
		boolean isUser = userRepository.existsByUid(uid);
		JwtClaimsSet claims;
		if (isUser) {
			// 우리 유저인경우
			claims = jwtProvider.forUser(uid);
		} else {
			// 우리 유저가 아닌경우
			int oauthRef = 3;
			if (authentication.toString().contains("kakao")) {
				oauthRef = MyRole.OAUTH_REF_KAKAO;
			} else if (authentication.toString().contains("google")) {
				oauthRef = MyRole.OAUTH_REF_GOOGLE;
			} else {
				oauthRef = MyRole.OAUTH_REF_NAVER;
			}
			claims = jwtProvider.forGuest(uid, oauthRef);
		}

		var jwtToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
		// 헤더에 추가
		response.addHeader(SecurityConstants.JWT_HEADER, "Bearer " + jwtToken);

		// < 쿠키 테스트
		Cookie jwtCookie = new Cookie("JWT_TOKEN", jwtToken);
		jwtCookie.setHttpOnly(true);// 클라이언트에서 접근 불가
		jwtCookie.setSecure(true); // HTTPS에서만 전송
		jwtCookie.setPath("/"); // 모든 경로에서 쿠키 전송
//		jwtCookie.setMaxAge(120);// 설정안하면 세션쿠키됨
		response.addCookie(jwtCookie);
		// 쿠키 >

		logger.info("\n IP: {}\n Body: {} \n", request.getRemoteAddr(),
				jwtToken.substring(jwtToken.indexOf('.') + 1, jwtToken.lastIndexOf('.')));
		System.out.println(jwtToken);
		if (isUser) {
			response.sendRedirect("http://localhost:3000/main");
		} else {
			response.sendRedirect("http://localhost:3000/signup");
		}
	}
}
