package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.service.FollowService;
import site.challenger.project_challenger.util.InsuUtils;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	@GetMapping("/FollowCount")
	public CommonResponseDTO getFollowCount(Authentication authentication,
			@RequestParam(required = true) long targetUserNo) {

		return followService.getFollowCount(targetUserNo);
	}

	@GetMapping("/FollowerCount")
	public CommonResponseDTO getFollowerCount(Authentication authentication,
			@RequestParam(required = true) long targetUserNo) {
		return followService.getFollowerCount(targetUserNo);
	}

	@GetMapping("/FollowDetail")
	public CommonResponseDTO getFollowDetail(Authentication authentication,
			@RequestParam(required = true) long targetUserNo,
			@RequestParam(required = false, defaultValue = "0") int page) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);
		return followService.getFollowDetail(requestUserNo, targetUserNo, page);
	}

	@GetMapping("/FollowerDetail")
	public CommonResponseDTO getFollowerDetail(Authentication authentication,
			@RequestParam(required = true) long targetUserNo,
			@RequestParam(required = false, defaultValue = "0") int page) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);
		return followService.getFollowerDetail(requestUserNo, targetUserNo, page);
	}

}
