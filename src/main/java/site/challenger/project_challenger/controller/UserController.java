package site.challenger.project_challenger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.service.UserService;
import site.challenger.project_challenger.util.InsuUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@PostMapping("/profile")
	public ResponseEntity<String> changeProfile(Authentication authentication, @RequestParam("file") MultipartFile file,
			HttpServletRequest request) {

		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		String savedFileName = userService.changeProfileImage(request, file, requestUserNo);

		return new ResponseEntity<>("파일 업로드 성공: " + savedFileName, HttpStatus.CREATED);
	}

}
