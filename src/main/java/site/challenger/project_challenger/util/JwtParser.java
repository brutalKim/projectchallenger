package site.challenger.project_challenger.util;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.JwtModel;

@Component
@RequiredArgsConstructor
public class JwtParser {
	private final JwtDecoder jwtDecoder;

	public JwtModel fromCookie(HttpServletRequest request) {

		String jwtToken = resolveTokenFromCookies(request);

		JwtModel jwtModel = jwtModelProvider(jwtToken);

		return jwtModel;
	}

	public JwtModel fromHeader(HttpServletRequest request) {

		String jwtToken = resolveToken(request);

		JwtModel jwtModel = jwtModelProvider(jwtToken);

		return jwtModel;
	}

	private JwtModel jwtModelProvider(String jwtToken) {

		JwtModel jwtModel = null;

		if (null != jwtToken) {

			Jwt jwt = jwtDecoder.decode(jwtToken);
			String userNo = jwt.getSubject();
			String nickname = jwt.getClaimAsString("nickname");
			jwtModel = new JwtModel(Long.valueOf(userNo), nickname);
		}

		if (jwtModel == null) {
			throw new RuntimeException("Something went wrong - JwtParser");
		}

		return jwtModel;

	}

	// 쿠키 읽기
	private String resolveTokenFromCookies(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("JWT_TOKEN".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	// 헤더 읽기
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
