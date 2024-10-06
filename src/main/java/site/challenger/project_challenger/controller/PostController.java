package site.challenger.project_challenger.controller;


import java.util.ArrayList;
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
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.post.PostCommentReqDTO;
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
	public CommonResponseDTO writePost(
	        Authentication authentication, 
	        @RequestParam("content") String content, 
	        @RequestParam(value = "images", required = false) List<MultipartFile> images
	        /*,@RequestParam(value = "images", required = false) List<Long>tagChallenges*/) {
	    Long userId = Long.parseLong(authentication.getName());
	    PostWriteServiceReqDTO req = new PostWriteServiceReqDTO(userId, content, images,null);
	    return postManagementService.writePost(req);
	}

	//포스트 조회
	@GetMapping("")
	public CommonResponseDTO getPost(@RequestParam(required = false) Long writerId ,@RequestParam(required = false) String type,@RequestParam(required = true) int page , Authentication authentication) {
		Long userId = Long.parseLong(authentication.getName());
		//writer Id가 존재하지 않을 경우 사용자의 포스트 접근
		if(writerId == null) {
			return postManagementService.getByUserId(userId,userId,page);
		}else {
			switch(type) {
			case"region":
				return null;
			case"follow:":
				return null;
			case "recommend":
				return postManagementService.getRecommendPost(userId, page);
			default:
				return postManagementService.getByUserId(writerId, userId , page);
			}
		}
	}
	//포스트 좋아요
	@GetMapping("/recommend")
	public CommonResponseDTO recommend(Authentication authentication,@RequestParam Long postNo) {
		Long userNo = Long.parseLong(authentication.getName());
		return postManagementService.recommend(new PostRecommendServiceReqDTO(userNo,postNo));
	}
	//포스트 코멘트 작성
	@PostMapping("/comment")
	public CommonResponseDTO writeComment(Authentication authentication, @RequestBody PostCommentReqDTO req) {
		Long writerNo = Long.parseLong(authentication.getName());
		return postManagementService.writeComment(writerNo, req.getPostNo(), req.getContent());
		
	}
	//포스트 코멘트 조회
	@GetMapping("/comment")
	public CommonResponseDTO Comment(@RequestParam Long postNo){
		return postManagementService.getComment(postNo);
	}
	//삭제는 추후 수정
	//포스트 코멘트 삭제
	@DeleteMapping("/comment")
	public ResponseEntity<String> deleteComment(@RequestParam Long commentNo, Authentication authentication){
		Long writerNo = Long.parseLong(authentication.getName());
		HttpStatus res = postManagementService.deleteComment(commentNo, writerNo);
		return new ResponseEntity<String>("Comment삭제 완료",res);
	}
	//삭제는 추후 수정
	//포스트 삭제
	@DeleteMapping("")
	public ResponseEntity<String> deletePost(@RequestParam Long PostNo, Authentication authentication) {
		Long userId = Long.parseLong(authentication.getName());
		HttpStatus res = postManagementService.deletePost(PostNo, userId); 
		return new ResponseEntity<String>("삭제 완료",res);
	}
	
}
