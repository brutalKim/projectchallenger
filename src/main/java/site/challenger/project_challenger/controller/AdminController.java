package site.challenger.project_challenger.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.admin.RestrictionDto;
import site.challenger.project_challenger.service.AdminService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

	private final AdminService adminService;

	@GetMapping("/main")
	public String getAdminPage() {
		return "/admin/index.html";
	}

	@GetMapping("/css")
	public ResponseEntity<Resource> getCssFile() {
		Resource resource = new ClassPathResource("static/admin/admin.css");

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "text/css").body(resource);
	}

	@GetMapping("/js")
	public ResponseEntity<Resource> getJsFile() {

		Resource resource = new ClassPathResource("static/admin/admin.js");

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "application/javascript").body(resource);

	}

	@ResponseBody
	@GetMapping("/report/posts")
	public CommonResponseDTO getReportedPosts(@RequestParam(required = false, defaultValue = "0") int page) {

		return adminService.getReportedPosts(page);
	}

	// 신고된 내역 가져오기
	@ResponseBody
	@GetMapping("/report")
	public CommonResponseDTO getReports(@RequestParam(required = true) String targetkind,
			@RequestParam(required = true) long targetno,
			@RequestParam(required = false, defaultValue = "0") int page) {

		return adminService.getReports(targetkind, targetno, page);
	}

	// 신고된 내역 숫자 가져오기
	@ResponseBody
	@GetMapping("/report/count")
	public CommonResponseDTO getReportsCount(@RequestParam(required = true) String targetkind,
			@RequestParam(required = true) long targetno) {

		return adminService.getReportsCount(targetkind, targetno);
	}

	// 신고내역 읽음 처리
	@ResponseBody
	@GetMapping("/read/report")
	public CommonResponseDTO readReport(@RequestParam(required = true) long reportNo) {

		return adminService.readReport(reportNo);
	}

	// 신고된 댓글 가져오기
	@ResponseBody
	@GetMapping("/report/comments")
	public CommonResponseDTO getReportedComments(@RequestParam(required = false, defaultValue = "0") int page) {

		return adminService.getReportedComments(page);
	}

	@ResponseBody
	@GetMapping("/report/challenges")
	public CommonResponseDTO getReportedChallenges(@RequestParam(required = false, defaultValue = "0") int page) {

		return adminService.getReportedChallenges(page);
	}

	@ResponseBody
	@GetMapping("/adminList")
	public CommonResponseDTO getAdminList() {

		return adminService.getAdminList();
	}

	@ResponseBody
	@GetMapping("/getUser")
	public CommonResponseDTO getUserByUserNickname(@RequestParam(required = true) String nickname) {

		return adminService.getUserByUserNickName(nickname);
	}

	@ResponseBody
	@GetMapping("/addAdmin")
	public CommonResponseDTO addAdmin(@RequestParam(required = true) String nickname) {

		return adminService.addAdmin(nickname);

	}

	@ResponseBody
	@GetMapping("/deleteAdmin")
	public CommonResponseDTO deleteAdmin(@RequestParam(required = true) String nickname) {

		return adminService.deleteAdmin(nickname);

	}

	@ResponseBody
	@PostMapping("/restriction")
	public CommonResponseDTO restrictUser(@Valid @RequestBody RestrictionDto restrictionDto) {

		return adminService.restrictUser(restrictionDto);
	}

	@ResponseBody
	@GetMapping("/noProblem")
	public CommonResponseDTO noProblem(@RequestParam(required = true) String targetkind,
			@RequestParam(required = true) long targetno) {

		return adminService.noProblem(targetkind, targetno);
	}

}
