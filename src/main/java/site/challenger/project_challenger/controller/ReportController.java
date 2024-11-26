package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.report.ReportRequestDto;
import site.challenger.project_challenger.service.ReportService;
import site.challenger.project_challenger.util.InsuUtils;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
	private final ReportService reportService;

	@PostMapping
	public CommonResponseDTO report(Authentication authentication, @RequestBody ReportRequestDto reportRequestDto) {
		long reportUserNo = InsuUtils.getRequestUserNo(authentication);

		return reportService.report(reportUserNo, reportRequestDto);
	}

}
