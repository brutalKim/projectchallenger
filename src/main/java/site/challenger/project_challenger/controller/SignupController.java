package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.service.MemberManagementService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class SignupController {
	private final MemberManagementService memberManagementService;
	@PostMapping(path = "/signup")
	public String signup(@RequestHeader("Authorization") String token , @RequestBody String body ,Authentication authentication) {
		System.out.println("회원가입 접근");
		 try {
			 	System.out.println(token);
			 	System.out.println("###################################################################");

			 	System.out.println(authentication.getName());
			 	memberManagementService.saveMember(authentication.getName(), body);
	            return "signupComplete";
	        } catch (Exception e) {
	            // 로그에 예외를 기록하고 클라이언트에 적절한 오류 응답을 반환
	        	e.printStackTrace();
	            System.err.println("Error processing request: " + e.getMessage());
	            return "signupErr";
	        }
	}
	@Getter @Setter
	public static class SignupDTO{
		private String nickname;
	}
}
 