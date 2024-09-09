package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
public class LoginController2 {
	@GetMapping("/1")
	public String loginSuccess(HttpSession session, Authentication authentication) {
		System.out.println(authentication);
		return "HI, ";
	}
	@GetMapping("/3")
	public String loginFailed2(Authentication authentication) {
		System.out.println(authentication);
		System.out.println(authentication.getName());
		System.out.println("logglgogloFaililil");

		return "HI, Faileddsadasdas";
	}

}
