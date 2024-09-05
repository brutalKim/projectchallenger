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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.Member;
import site.challenger.project_challenger.service.MemberManagementService;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
	private final MemberManagementService memberManagementService;
	private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);
	private final JwtAuthenticationResource jwtAuthenticationResource;

// Oauth2 인증을 하면 얘가 처리함 정보->
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 인증 성공 시 실행할 커스텀 로직을 여기에 작성
		logger.info("CustomOAuth2SuccessHandler 호출됨. 인증 성공!");
		Optional<Member> optionalMemberVO = memberManagementService.searchMember(authentication.getName());
		ResponseDTO responseDTO;

		// 여기서 authenticate에 역할도 넣어줘야할듯? 이용자가 비어있는지 아닌지 확인하고 있으면 역할도 가져와서
		// jwtAuthenticationResource.authenticate() 호출할때
		// 넘겨줘야함 그래야 jwt발행할때 역할 담을 수 있음
		String jwt = jwtAuthenticationResource.authenticate(authentication);
		jwt = jwt.replace("JwtResponse[token=", "");
		jwt = jwt.replace("]", "");
		if (optionalMemberVO.isEmpty()) {
			responseDTO = new ResponseDTO(jwt, false);
			// 테스트로 자동 회원가입
			// json형식으로 토큰,회원가입 여부를 리턴
			String jsonResponse = String.format("{\"token\":\"%s\",\"isMember\":%b}", responseDTO.getToken(),
					responseDTO.isMember());
			response.getWriter().write(jsonResponse);
			response.getWriter().flush();
		} else {
			// json형식으로 토큰,회원가입 여부를 리턴
			responseDTO = new ResponseDTO(jwt, true);
			;
			String jsonResponse = String.format("{\"token\":\"%s\",\"isMember\":%b}", responseDTO.getToken(),
					responseDTO.isMember());
			response.getWriter().write(jsonResponse);
			response.getWriter().flush();
		}
	}

	// reponse DTO
	// 아필요없노 이거 ㅅㅂㅋㅋ
	@Getter
	@Setter
	public class ResponseDTO {
		private String token;
		private boolean isMember;

		public ResponseDTO(String token, boolean isMember) {
			this.token = token;
			this.isMember = isMember;
		}
	}
}
