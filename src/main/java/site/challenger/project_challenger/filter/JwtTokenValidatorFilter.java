package site.challenger.project_challenger.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

	private final JwtDecoder jwtDecoder;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 헤더먼저 검사
		String jwtToken = null;
		if(resolveToken(request) != null) {
			jwtToken = resolveToken(request);
		}
		//쿠키 토큰 검사
		if(resolveTokenFromCookies(request) != null) {
			jwtToken = resolveTokenFromCookies(request);
		}
		if (null != jwtToken) {
			// 토큰이 있으면 검증
			String body = jwtToken.substring(jwtToken.indexOf('.') + 1, jwtToken.lastIndexOf('.'));
			try {
				Jwt jwt = jwtDecoder.decode(jwtToken);
				String username = jwt.getSubject();
				String authorities = jwt.getClaimAsString("authorities");
				Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
						AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (Exception e) {
				logger.info("\n IP: {}\n errorMessage: {}\n Body: {} \n", request.getRemoteAddr(), e.getMessage(),
						body);
				throw new BadCredentialsException("Invaild Token received! - jvf");
			}

		}
		filterChain.doFilter(request, response);
	} 

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// TODO Auto-generated method stub
		// 로그인 하러 갈 떄는 토큰이 없으므로 통과시켜 줘야함 근대왜 ? 안해줘도됨?
		//
		return super.shouldNotFilter(request);
	}

	// 쿠키 읽기
	public String resolveTokenFromCookies(HttpServletRequest request) {
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
