package site.challenger.project_challenger.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.domain.PostComment;
import site.challenger.project_challenger.domain.PostRecommend;
import site.challenger.project_challenger.domain.Users;

import site.challenger.project_challenger.dto.ResDTO;
import site.challenger.project_challenger.dto.post.PostCommentResDTO;
import site.challenger.project_challenger.dto.post.PostDTO;
import site.challenger.project_challenger.dto.post.PostGetResDTO;
import site.challenger.project_challenger.dto.post.PostRecommendResDTO;
import site.challenger.project_challenger.dto.post.PostRecommendServiceReqDTO;
import site.challenger.project_challenger.dto.post.PostWriteServiceReqDTO;
import site.challenger.project_challenger.repository.PostCommentRepository;
import site.challenger.project_challenger.repository.PostRecommendRepository;
import site.challenger.project_challenger.repository.PostRepository;
import site.challenger.project_challenger.repository.UserRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class PostManagementService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostRecommendRepository postRecommendRepository;
	private final PostCommentRepository postCommentRepository;

	public ResDTO writePost(PostWriteServiceReqDTO req) {
		ResDTO res = null;
		Long writerId = req.getWriterId();
		String content = req.getContent();
		try {
			Optional<Users> writer = userRepository.findById(writerId);
			if(writer.isPresent()) {
				Post newPost = new Post(writer.get(),content);
				postRepository.save(newPost);
				res = new ResDTO(HttpStatus.CREATED,"성공");
			}
		}catch(Exception e) {
			res = new ResDTO(HttpStatus.CONFLICT,"서버 내부오류");
		}finally {
			return res;
			}
	}
	public PostGetResDTO getByUserId(Long writerNo ,Long userNo) {
		PostGetResDTO res = null;
		try {
			Optional<Users>optionalWriter = userRepository.findById(writerNo);
			Optional<Users>optionalUser = userRepository.findById(userNo);
			if(optionalWriter.isPresent() && optionalUser.isPresent()) {
				Users writer = optionalWriter.get();
				Users user = optionalUser.get();
				ArrayList<PostDTO> posts = postRepository.getPostByWriterAndUser(writer.getNo(),user.getNo());
				for(PostDTO postDTO :posts) {
					Long commentCount = postCommentRepository.countByPostNo(postDTO.getNo());
					postDTO.setCommentCount(commentCount);
				}
				if(posts.size() == 0) {
					res = new PostGetResDTO(HttpStatus.NOT_FOUND,writer.getNickname()+"이 작성한 글을 찾을 수 없습니다.",null);
				}else {
					res = new PostGetResDTO(HttpStatus.OK,writer.getNickname()+"님이 작성한글",posts);
				}
			}else {
				res = new PostGetResDTO(HttpStatus.NOT_FOUND, "유효하지않은 작성자", null);
			}
		}catch(Exception e) {
			res = new PostGetResDTO(HttpStatus.CONFLICT,"내부 서버오류",null);
		}finally {
			return res;
		}
	}
	public PostRecommendResDTO recommend(PostRecommendServiceReqDTO req) {
		PostRecommendResDTO res = null;
		Long userId = req.getUserId();
		Long postNo = req.getPostNo();
		try {
			Optional<Users> optionalRecommender = userRepository.findById(userId);
			Optional<Post> optionalPost = postRepository.findById(postNo);
			//사용자와 포스트가 존재할때만
			if(optionalRecommender.isPresent() && optionalPost.isPresent()) {
				PostRecommend postRecommend = new PostRecommend(optionalRecommender.get(),optionalPost.get());
				Optional<PostRecommend> optionalPostRecommend = postRecommendRepository.findById(postRecommend.getPK());
				//추천한 이력이 없을시 추천
				if(optionalPostRecommend.isEmpty()) {
					postRecommendRepository.save(postRecommend);
					Post post = optionalPost.get();
					post.incrementRecommend();
					postRepository.save(post);
					res = new PostRecommendResDTO(HttpStatus.OK,"추천 성공",true,post.getRecommend());
				}else {
					//추천한 이력이 있을시 비추
					PostRecommend existedRecommend = optionalPostRecommend.get();
					postRecommendRepository.deleteById(existedRecommend.getPK());
					Post post = optionalPost.get();
					post.decrementRecommend();
					postRepository.save(post);
					res = new PostRecommendResDTO(HttpStatus.OK,"추천 취소 성공",false,post.getRecommend());
				}
			}else { 
				res = new PostRecommendResDTO(HttpStatus.BAD_REQUEST,"잘못된 요청",false,0L);
			}
		}catch(Exception e) {
			res = new PostRecommendResDTO(HttpStatus.CONFLICT, e.toString() ,false,0L);
		}finally {
			return res;
		}
	}
	public ResDTO writeComment(Long writerNo,Long postNo ,String content) {
		ResDTO res = null;
		try {
			Optional<Users> optionalWriter = userRepository.findById(writerNo);
			Optional<Post> optionalPost = postRepository.findById(postNo);
			if(optionalWriter.isPresent() && optionalPost.isPresent()) {
				Users user = optionalWriter.get();
				Post post = optionalPost.get();
				PostComment postComment = new PostComment(user,post,content);
				postCommentRepository.save(postComment);
				res = new ResDTO(HttpStatus.CREATED,"작성 성공");
			}else if(optionalWriter.isEmpty()){
				res = new ResDTO(HttpStatus.UNAUTHORIZED,"잘못된 사용자");
			}else {
				res = new ResDTO(HttpStatus.BAD_REQUEST,"잘못된 계시글 접근");
			}
		}catch(Exception e) {
			res = new ResDTO(HttpStatus.CONFLICT,e.toString());
		}finally {
			return res;
		}
	}
	/*
	public PostCommentResDTO getComment(Long postNo) {
		PostCommentResDTO res = null;
		try {
			ArrayList<PostComment> comments = postCommentRepository.findAllByPostNo(postNo);
			res = new PostCommentResDTO(HttpStatus.OK, postNo+"번 comment 조회 성공", comments);
		}catch(Exception e) {
			res = (PostCommentResDTO) new ResDTO(HttpStatus.CONFLICT,e.toString());
		}finally {
			return res;
		}
	}*/
}