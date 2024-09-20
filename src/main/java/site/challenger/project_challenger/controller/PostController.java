package site.challenger.project_challenger.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.dto.ResDTO;
import site.challenger.project_challenger.dto.post.CommentWriteResDTO;
import site.challenger.project_challenger.dto.post.PostCommentReqDTO;
import site.challenger.project_challenger.dto.post.PostCommentResDTO;
import site.challenger.project_challenger.dto.post.PostGetResDTO;
import site.challenger.project_challenger.dto.post.PostRecommendResDTO;
import site.challenger.project_challenger.dto.post.PostRecommendServiceReqDTO;
import site.challenger.project_challenger.dto.post.PostWriteServiceReqDTO;
import site.challenger.project_challenger.service.PostManagementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
	private final PostManagementService postManagementService;
	//포스트 작성
	@PostMapping("")
	public ResponseEntity<ResDTO> writePost(
	        Authentication authentication, 
	        @RequestParam("content") String content, 
	        @RequestParam(value = "images", required = false) List<MultipartFile> images) {
	    Long userId = Long.parseLong(authentication.getName());
	    PostWriteServiceReqDTO req = new PostWriteServiceReqDTO(userId, content, images);
	    ResDTO res = postManagementService.writePost(req);
	    
	    return new ResponseEntity<>(res, res.getStatus());
	}

	//포스트 삭제
	@DeleteMapping("")
	public ResponseEntity<String> deletePost(@RequestParam Long PostNo, Authentication authentication) {
		Long userId = Long.parseLong(authentication.getName());
		HttpStatus res = postManagementService.deletePost(PostNo, userId);
		return new ResponseEntity<String>("삭제 완료",res);
	}
	//포스트 조회
	@GetMapping("")
	public ResponseEntity<PostGetResDTO> getPost(@RequestParam(required = false) Long writerId ,Authentication authentication) {
		Long userId = Long.parseLong(authentication.getName());
		PostGetResDTO res= null;
		if(writerId == null) {
			res = postManagementService.getByUserId(userId,userId);
		}else {
			res = postManagementService.getByUserId(writerId,userId);
		}
		return new ResponseEntity<PostGetResDTO>(res,res.getStatus());
	}
	//포스트 좋아요
	@GetMapping("/recommend")
	public ResponseEntity<PostRecommendResDTO> recommend(Authentication authentication,@RequestParam Long postNo) {
		Long userNo = Long.parseLong(authentication.getName());
		PostRecommendResDTO res =postManagementService.recommend(new PostRecommendServiceReqDTO(userNo,postNo));
		return new ResponseEntity<PostRecommendResDTO>(res,res.getStatus());
	}
	//포스트 코멘트 작성
	@PostMapping("/comment")
	public ResponseEntity<CommentWriteResDTO> writeComment(Authentication authentication, @RequestBody PostCommentReqDTO req) {
		Long writerNo = Long.parseLong(authentication.getName());
		CommentWriteResDTO res = postManagementService.writeComment(writerNo, req.getPostNo(), req.getContent());
		return new ResponseEntity<CommentWriteResDTO>(res,res.getStatus());
	}
	//포스트 코멘트 조회
	@GetMapping("/comment")
	public ResponseEntity<PostCommentResDTO> Comment(@RequestParam Long postNo){
		PostCommentResDTO res = postManagementService.getComment(postNo);
		return new ResponseEntity<PostCommentResDTO>(res,res.getStatus());
	}
	//포스트 코멘트 삭제
	@DeleteMapping("/comment")
	public ResponseEntity<String> deleteComment(@RequestParam Long commentNo, Authentication authentication){
		Long writerNo = Long.parseLong(authentication.getName());
		HttpStatus res = postManagementService.deleteComment(commentNo, writerNo);
		return new ResponseEntity<String>("Comment삭제 완료",res);
	}
}
