package site.challenger.project_challenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.post.PostWriteReqDTO;
import site.challenger.project_challenger.dto.post.PostWriteServiceReqDTO;
import site.challenger.project_challenger.service.PostManagementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
	private final PostManagementService postManagementService;
	@PostMapping("/write")
	public boolean writePost(Authentication authentication, @RequestBody PostWriteReqDTO req) {
		System.out.println(authentication.getName());
		return true;
	}
}
