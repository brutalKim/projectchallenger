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
import site.challenger.project_challenger.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
	private final PostManagementService postManagementService;
	private final UserService userService;
	//포스트 작성
	@PostMapping("")
	public CommonResponseDTO writePost(
	        Authentication authentication, 
	        @RequestParam("content") String content, 
	        @RequestParam(value = "images", required = false) List<MultipartFile> images
	        ,@RequestParam(required = false) List<Long>tagChallenges) {
	    Long userId = Long.parseLong(authentication.getName());
	    PostWriteServiceReqDTO req = new PostWriteServiceReqDTO(userId, content, images,tagChallenges);
	    return postManagementService.writePost(req);
	}

	//포스트 조회
	@GetMapping("")
	public CommonResponseDTO getPost(@RequestParam(required = false) List<Long> writerId ,@RequestParam(required = false) String type,@RequestParam(required = true) int page , @RequestParam(required = false) String keyWord , Authentication authentication) {
		Long userId = Long.parseLong(authentication.getName());
		//writer Id가 존재하지 않을 경우 사용자의 포스트 접근
		switch(type) {
			case"region":
				return null;
			case"search":
				return postManagementService.getByKeyWord(userId, page, keyWord);
			case"follow":
				return postManagementService.getPostByFollow(userId, page);
			case "recommend":
				return postManagementService.getRecommendPost(userId, page);
			case "user":
				if(writerId==null) {
					List<Long> writerNo = new ArrayList<>();
					writerNo.add(userId);
					return postManagementService.getByUserId(writerNo,userId,page);
				}
				return postManagementService.getByUserId(writerId, userId , page);
			default:
				return null;
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
	
	//TODO:포스트 코멘트 추천
	@GetMapping("/comment/recommend")
	public CommonResponseDTO recommendComment(Authentication authentication,@RequestParam(required = true) Long commentNo) {
		Long userId = Long.parseLong(authentication.getName());
		return postManagementService.recommendComment(userId, commentNo);
	}
	//포스트 코멘트 조회
	@GetMapping("/comment")
	public CommonResponseDTO Comment(Authentication authentication,@RequestParam Long postNo){
		Long userNo = Long.parseLong(authentication.getName());
		return postManagementService.getComment(userNo,postNo);
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
