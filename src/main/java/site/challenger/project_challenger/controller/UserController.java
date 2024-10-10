package site.challenger.project_challenger.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
	//유저 상세정보
	@GetMapping("/detail")
	public CommonResponseDTO getDetail(Authentication authentication, @RequestParam(required = false)Long targetNo) {
		Long userNo = Long.parseLong(authentication.getName());
		//유저 no이 존재하지않을경우 자기정보 조회
		if(targetNo == null) {
			Long userSelfNo = Long.parseLong(authentication.getName());
			return userService.getUserDetail(userNo,userSelfNo);
		}
		//유저 no가 존재할경우 타 유저의 정보 조회
		return userService.getUserDetail(userNo,targetNo);
	}
	//유저 follow
	@GetMapping("/follow")
	public CommonResponseDTO followUser(Authentication authentication, @RequestParam(required = true)Long userNo) {
		Long userSelfNo = Long.parseLong(authentication.getName());
		return userService.followUser(userSelfNo, userNo);
	}
}
