package site.challenger.project_challenger.security;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.MemberVO;
import site.challenger.project_challenger.service.MemberManagementService;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private final MemberManagementService memberManagementService;
	private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);
	private final JwtAuthenticationResource jwtAuthenticationResource;

// Oauth2 인증을 하면 얘가 처리함 정보->
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,Authentication authentication) throws IOException, ServletException {
		// 인증 성공 시 실행할 커스텀 로직을 여기에 작성
		logger.info("CustomOAuth2SuccessHandler 호출됨. 인증 성공!");
		Optional<MemberVO> optionalMemberVO = memberManagementService.searchMember(authentication.getName());
		String jwt = jwtAuthenticationResource.authenticate(authentication);
		jwt = jwt.replace("JwtResponse[token=", "");
		jwt = jwt.replace("]", "");
		if(optionalMemberVO.isEmpty()) {
			System.out.println("no member");
			//비회원일시 세션발급
			HttpSession session = request.getSession(true);
			//세션에 response 넣음
			ResponseDTO responseDTO = new ResponseDTO(jwt,false);
			session.setAttribute("token", responseDTO);
			response.sendRedirect("http://localhost:3000/authentication");
		}else {
			System.out.println("is member");
			HttpSession session = request.getSession(true);
			//세션에 response 넣음
			ResponseDTO responseDTO = new ResponseDTO(jwt,true);
			session.setAttribute("token", responseDTO);
			response.sendRedirect("http://localhost:3000/authentication");
		}
	}
	//reponse DTO
	//아필요없노 이거 ㅅㅂㅋㅋ
	@Getter
	@Setter
	public static class ResponseDTO{
		private String token;
		private boolean isMember;
		public ResponseDTO(String token,boolean isMember) {
			this.token = token;
			this.isMember = isMember;
		}
	}
}
