package site.challenger.project_challenger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@PostMapping("/profile/{userNo}")
	public ResponseEntity<String> changeProfile(@RequestParam("file") MultipartFile file, HttpServletRequest request,
			@PathVariable long userNo) {

		String savedFileName = userService.changeProfileImage(request, file, userNo);

		return new ResponseEntity<>("파일 업로드 성공: " + savedFileName, HttpStatus.CREATED);
	}

}
