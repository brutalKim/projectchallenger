package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController2 {
	@GetMapping("/1")
	public String loginSuccess(Authentication authentication) {
		System.out.println(authentication);
		System.out.println("logincon");

		return "HI, ";
	}

	@GetMapping("/2")
	public String loginFailed(Authentication authentication) {
		System.out.println(authentication);
		System.out.println("logglgogloFaililil");

		return "HI, Failed";
	}

	@GetMapping("/3")
	public String loginFailed2(Authentication authentication) {
		System.out.println(authentication);
		System.out.println(authentication.getName());
		System.out.println("logglgogloFaililil");

		return "HI, Faileddsadasdas";
	}

}
