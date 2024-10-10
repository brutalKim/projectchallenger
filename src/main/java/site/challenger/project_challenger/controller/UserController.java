package site.challenger.project_challenger.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.service.UserService;
import site.challenger.project_challenger.util.InsuUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@PostMapping(value = "/profile/image", headers = "Content-Type=multipart/form-data")
	public CommonResponseDTO changeProfile(Authentication authentication,
			@RequestParam(required = true, value = "image") MultipartFile image, HttpServletRequest request) {

		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		String savedFileName = userService.changeProfileImage(request, image, requestUserNo);

		Map<String, Object> body = new HashMap<>();
		body.put("savedFileName", savedFileName);
		return new CommonResponseDTO(body, HttpStatus.OK, "이미지 저장 성공 :", null, true);
	}

}
