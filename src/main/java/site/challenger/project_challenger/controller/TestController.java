package site.challenger.project_challenger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@GetMapping("/api/v1/test/1")
	public String test1() {
		return "테스트";
	}

}
