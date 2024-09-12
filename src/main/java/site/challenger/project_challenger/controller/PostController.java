package site.challenger.project_challenger.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;<<<<<<< 0912
import site.challenger.project_challenger.dto.ResDTO;
import site.challenger.project_challenger.dto.post.PostWriteReqDTO;
import site.challenger.project_challenger.dto.post.PostWriteServiceReqDTO;
import site.challenger.project_challenger.service.PostManagementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
	private final PostManagementService postManagementService;
	@PostMapping("/write")

	public ResponseEntity<ResDTO> writePost(Authentication authentication, @RequestBody PostWriteReqDTO req) {
		Long userId = Long.parseLong(authentication.getName());
		ResDTO res = postManagementService.writePost(new PostWriteServiceReqDTO(userId,req.getContent()));
		return new ResponseEntity<ResDTO>(res,HttpStatus.OK);

	}
}
