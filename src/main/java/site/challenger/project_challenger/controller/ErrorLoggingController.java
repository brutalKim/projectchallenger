package site.challenger.project_challenger.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.challenger.project_challenger.dto.ErrorInfo;

@RestController
@RequestMapping("/debug")
public class ErrorLoggingController {

	@PostMapping
	public ResponseEntity<Void> testLogError(Authentication authentication, @RequestBody ErrorInfo errorInfo) {

		System.out.println("받은 에러" + errorInfo);

		return ResponseEntity.ok().build();
	}

}
