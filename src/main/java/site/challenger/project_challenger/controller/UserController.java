package site.challenger.project_challenger.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.CommonResponseDTO;

import site.challenger.project_challenger.service.ChallengeService;

import site.challenger.project_challenger.dto.user.UserRequestDTO;

import site.challenger.project_challenger.service.UserService;
import site.challenger.project_challenger.util.InsuUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	private final ChallengeService challengeService;
	private final UserService userService;

	@DeleteMapping("/profile/image")
	public CommonResponseDTO deleteProfileImage(Authentication authentication) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		return userService.deleteProfileImage(requestUserNo);
	}

	// 이미지 변경
	@PostMapping(value = "/profile/image", headers = "Content-Type=multipart/form-data")
	public CommonResponseDTO changeProfileImage(Authentication authentication,
			@RequestParam(required = true, value = "image") MultipartFile image, HttpServletRequest request) {

		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		String savedFileName = userService.changeProfileImage(request, image, requestUserNo);

		Map<String, Object> body = new HashMap<>();
		body.put("savedFileName", savedFileName);
		return new CommonResponseDTO(body, HttpStatus.OK, "이미지 저장 성공 :", null, true);
	}

	// 유저 상세정보
	@GetMapping("/detail")
	public CommonResponseDTO getDetail(Authentication authentication, @RequestParam(required = false) Long targetNo) {
		Long userNo = Long.parseLong(authentication.getName());
		// 유저 no이 존재하지않을경우 자기정보 조회
		if (targetNo == null) {
			Long userSelfNo = Long.parseLong(authentication.getName());
			return userService.getUserDetail(userNo, userSelfNo);
		}
		// 유저 no가 존재할경우 타 유저의 정보 조회
		return userService.getUserDetail(userNo, targetNo);
	}

	// 유저 follow
	@GetMapping("/follow")
	public CommonResponseDTO followUser(Authentication authentication, @RequestParam(required = true) Long userNo) {
		Long userSelfNo = Long.parseLong(authentication.getName());
		return userService.followUser(userSelfNo, userNo);
	}

	//유저 구독중 챌린지 조회
	@GetMapping("/subchallenge")
	public CommonResponseDTO getSubChallenge(Authentication authentication, @RequestParam(required = false)Long targetUserNo) {
		//타겟 유저가 null일경우 자기가 구독한 챌린지 조회
		Long userNo = Long.parseLong(authentication.getName());
		if(targetUserNo == null) {
			return challengeService.getAllSubcribedChallengeByUserNo(userNo, userNo, 0);
		} 
		//타겟유저가 존재할경우 타겟유저의 구독한 챌린지 조회
		return challengeService.getAllSubcribedChallengeByUserNo(targetUserNo, userNo, 0);
	}

	// 유저 닉네임과, 유저 설명 변경
	@PostMapping("/profile")
	public CommonResponseDTO changeUserDetail(Authentication authentication, @RequestBody UserRequestDTO userRequestDTO,
			HttpServletRequest request) {

		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		return userService.changeUserDetail(requestUserNo, userRequestDTO);
	}

	// 유저 닉네임이 존재하는지
	@GetMapping("/nickname")
	public CommonResponseDTO isExistNickName(@RequestParam(required = true) String nickname) {
		return userService.existsByNickName(nickname);
	}

	
}
