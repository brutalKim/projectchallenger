package site.challenger.project_challenger.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.challenger.project_challenger.dto.ErrorInfo;

@RestController
@RequestMapping("/api/v1/debug")
public class ErrorLoggingController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PostMapping
	public ResponseEntity<Void> testLogError(Authentication authentication, @RequestBody ErrorInfo errorInfo) {

		logger.debug("받은 에러: " + errorInfo);

		return ResponseEntity.ok().build();
	}

}
