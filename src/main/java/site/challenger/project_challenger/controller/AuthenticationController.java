//package site.challenger.project_challenger.controller;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.SessionAttribute;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import site.challenger.project_challenger.security.CustomOAuth2SuccessHandler.ResponseDTO;
//import site.challenger.project_challenger.service.MemberManagementService;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/authentication")
//public class AuthenticationController {
//	private final MemberManagementService memberManagementService;
//	@PostMapping("/token")
//	public ResponseDTO getToken(@SessionAttribute(name ="token") ResponseDTO token,HttpServletRequest request) {
//		ResponseDTO returnToken = token;
//		HttpSession session = request.getSession(false);
//		session.invalidate();
//		return returnToken;
//	}
//	@PostMapping("/signup")
//	public boolean signup(Authentication authentication, @RequestBody SignupDTO signupDTO) {
//		return memberManagementService.saveMember(authentication.getName(), signupDTO.getNickname());
//	}
//	@Getter
//	@Setter
//	public static class SignupDTO{
//		private String nickname;
//	}
//	@Getter
//	@Setter
//	public static class memberDTO{
//		private String nickname;
//	}
//}
