package site.challenger.project_challenger.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import site.challenger.project_challenger.dto.Test;

@RestController
public class LoginController2 {
	@GetMapping("/1")
	public Test loginSuccess(HttpSession session, Authentication authentication) {
		System.out.println(authentication);
		Map<String, Object> map = new HashMap<>();
		List<String> list = new ArrayList<>();
		list.add("testList");
		list.add("testList2");
		list.add("testList3");
		map.put("username", "test1");
		map.put("TESTString3", 1);
		map.put("TESTString55", true);
		map.put("TESTString", list);
		return new Test(map, HttpStatus.OK, "message", "redirectURL", true);
	}

	@GetMapping("/3")
	public String loginFailed2() {
		return "로그인 성공ㅇㅇㅇㅇㅇ";
	}

}
