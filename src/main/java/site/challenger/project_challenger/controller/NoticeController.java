package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.user.TargetRequestDTO;
import site.challenger.project_challenger.service.NoticeService;
import site.challenger.project_challenger.util.InsuUtils;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notice")
public class NoticeController {

	private final NoticeService noticeService;

	// notice 수 가져오기 except kind = 'notice'
	@GetMapping("/count")
	public CommonResponseDTO getNoticeCount(Authentication authentication) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		return noticeService.getNoticeCount(requestUserNo);
	}

	// notice 가져오기
	@GetMapping
	public CommonResponseDTO getNotice(Authentication authentication) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		return noticeService.getNotice(requestUserNo);
	}

	// notice 읽음 처리하기 -> 엔티티 지우기 -> except kind 'notice'
	@PostMapping("/isOver")
	public CommonResponseDTO deleteNotice(Authentication authentication,
			@RequestBody TargetRequestDTO targetRequestDTO) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		return noticeService.deleteNotice(requestUserNo, targetRequestDTO.getTargetNoArray());
	}

	@PostMapping("/read")
	public CommonResponseDTO readNotice(Authentication authentication, @RequestBody TargetRequestDTO targetRequestDTO) {
		long requestUserNo = InsuUtils.getRequestUserNo(authentication);

		return noticeService.readNotice(requestUserNo, targetRequestDTO.getTargetNoArray());
	}

}
