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
import lombok.Getter;
// <<<<<<< insu0909-1
// =======
// import jakarta.servlet.http.HttpSession;
// import lombok.Builder;
// import lombok.Getter;
// >>>>>>> main
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
// <<<<<<< insu0909-1
// =======
// 		// 인증 성공 시 실행할 커스텀 로직을 여기에 작성
// 		logger.info("CustomOAuth2SuccessHandler 호출됨. 인증 성공!");
// 		Optional<MemberVO> optionalMemberVO = memberManagementService.searchMember(authentication.getName());
// 		String jwt = jwtAuthenticationResource.authenticate(authentication);
// 		jwt = jwt.replace("JwtResponse[token=", "");
// 		jwt = jwt.replace("]", "");
// 		if(optionalMemberVO.isEmpty()) {
// 			System.out.println("no member");
// 			//비회원일시 세션발급
// 			HttpSession session = request.getSession(true);
// 			//세션에 response 넣음
// 			ResponseDTO responseDTO = new ResponseDTO(jwt,false);
// 			session.setAttribute("token", responseDTO);
// 			response.sendRedirect("http://localhost:3000/authentication");
// 		}else {
// 			System.out.println("is member");
// 			HttpSession session = request.getSession(true);
// 			//세션에 response 넣음
// 			ResponseDTO responseDTO = new ResponseDTO(jwt,true);
// 			session.setAttribute("token", responseDTO);
// 			response.sendRedirect("http://localhost:3000/authentication");
// 		Optional<Member> optionalMemberVO = memberManagementService.searchMember(authentication.getName());
// 		ResponseDTO responseDTO;
// >>>>>>> main
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

//		var claims = JwtClaimsSet.builder()
//				// 발급자
//				.issuer("project Challenge")
//				// 발급시간
//				.issuedAt(Instant.now())
//				// 만료 시간
//				.expiresAt(Instant.now().plusSeconds(60 * 15))
//				// subject - username - getName 한걸로 데이터베이스에서 꺼내야함 primary key를
//				.subject(authentication.getName())
//				// 닉넴, 지역번호 등등, 꺼내놓으면 편할 정보 다 꺼내놓는게 좋음
//				.claim("nick", authentication)
//				// 이거 데이터 베이스에서 꺼내야함
//				.claim("authorities", MyRole.ROLE_GUEST).build();

		var jwtToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
		// 헤더에 추가
		response.addHeader(SecurityConstants.JWT_HEADER, "Bearer " + jwtToken);

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
		System.out.println(jwtToken);
		response.sendRedirect("/1");
	}
}
