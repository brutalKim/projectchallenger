package site.challenger.project_challenger.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import site.challenger.project_challenger.repository.UsersRepository;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

	private JwtAuthenticationResource jwtAuthenticationResource;

	private UsersRepository repository;

	public CustomOAuth2SuccessHandler(JwtAuthenticationResource jwtAuthenticationResource, UsersRepository repository) {
		super();
		this.jwtAuthenticationResource = jwtAuthenticationResource;
		this.repository = repository;
	}

// Oauth2 인증을 하면 얘가 처리함 정보->
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 인증 성공 시 실행할 커스텀 로직을 여기에 작성
		logger.info("CustomOAuth2SuccessHandler 호출됨. 인증 성공!");

//		// 인증 성공 후 세션 무효화
//		HttpSession session = request.getSession(false);
//		if (session != null) {
//			session.invalidate();
//		}
//
//		// SecurityContext에 인증 정보를 저장하지 않음
//		// 필요 시, SecurityContextHolder.clearContext();를 호출하여 확실히 제거할 수 있음

		// 토큰 세부설정법.
		System.out.println(authentication);
		System.out.println(authentication.getName());
		// 데이터베이스랑 비교

		if (repository.findById(authentication.getName()).orElse(null) == null) {
			// 회원가입 유도
			String jwt = jwtAuthenticationResource.authenticate(authentication);
			jwt = jwt.replace("JwtResponse[token=", "");
			jwt = jwt.replace("]", "");

			String redirectUrl = UriComponentsBuilder.fromUriString("/2").queryParam("accessToken", jwt).build()
					.toUriString();
			response.sendRedirect(redirectUrl);
		} else {
			// 인증
			String jwt = jwtAuthenticationResource.authenticate(authentication);
			jwt = jwt.replace("JwtResponse[token=", "");
			jwt = jwt.replace("]", "");

			String redirectUrl = UriComponentsBuilder.fromUriString("/1").queryParam("accessToken", jwt).build()
					.toUriString();

			// 원하는 페이지로 리다이렉트
			response.sendRedirect(redirectUrl);
		}

		//
//		String jwt = jwtAuthenticationResource.authenticate(authentication);
//		jwt = jwt.replace("JwtResponse[token=", "");
//		jwt = jwt.replace("]", "");
//
//		String redirectUrl = UriComponentsBuilder.fromUriString("/1").queryParam("accessToken", jwt).build()
//				.toUriString();
//
//		// 원하는 페이지로 리다이렉트
//		response.sendRedirect(redirectUrl);
	}

//	@PostMapping("/signUp")
//	public 
}
