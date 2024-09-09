package site.challenger.project_challenger.security;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private static Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);
	private final JwtEncoder jwtEncoder;

// Oauth2 인증을 하면 얘가 처리함 정보->
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String id = authentication.getName();
		String email;
		LocalDateTime now = LocalDateTime.now();

		var claims = JwtClaimsSet.builder()
				// 발급자
				.issuer("project Challenge")
				// 발급시간
				.issuedAt(Instant.now())
				// 만료 시간
				.expiresAt(Instant.now().plusSeconds(60 * 15))
				// subject - username - getName 한걸로 데이터베이스에서 꺼내야함 primary key를
				.subject(authentication.getName())
				// 닉넴, 지역번호 등등, 꺼내놓으면 편할 정보 다 꺼내놓는게 좋음
//				.claim("nick", authentication)
				// 이거 데이터 베이스에서 꺼내야함
				.claim("authorities", "역할 권한").build();

		var jwtToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
// 		지우지말것
//		response.addHeader(SecurityConstants.JWT_HEADER, "Bearer " + JwtToken);

		// < 쿠키 테스트
		Cookie jwtCookie = new Cookie("JWT_TOKEN", jwtToken);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setSecure(true);
		jwtCookie.setPath("/");
//		jwtCookie.setMaxAge(120);// 설정안하면 세션쿠키됨
		response.addCookie(jwtCookie);
		// 쿠키 >

		logger.info("\n IP: {}\n Body: {} \n", request.getRemoteAddr(),
				jwtToken.substring(jwtToken.indexOf('.') + 1, jwtToken.lastIndexOf('.')));

		response.sendRedirect("/3");

	}

	private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
		Set<String> authoritiesSet = new HashSet<>();
		for (GrantedAuthority authority : collection) {
			authoritiesSet.add(authority.getAuthority());
		}
		return String.join(",", authoritiesSet);
	}

}
