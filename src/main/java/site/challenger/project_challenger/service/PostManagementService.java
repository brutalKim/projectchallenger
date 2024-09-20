package site.challenger.project_challenger.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.domain.PostComment;
import site.challenger.project_challenger.domain.PostImage;
import site.challenger.project_challenger.domain.PostRecommend;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.ResDTO;
import site.challenger.project_challenger.dto.post.CommentWriteResDTO;
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
import site.challenger.project_challenger.util.PostImageManager;
import site.challenger.project_challenger.util.PostImageManager;

@RequiredArgsConstructor
@Service
public class PostManagementService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostRecommendRepository postRecommendRepository;
	private final PostCommentRepository postCommentRepository;
	//포스트 이미지 관리 컴포넌트
	private final PostImageManager postImageManager;
	
	//Post작성
	@Transactional
	public ResDTO writePost(PostWriteServiceReqDTO req) {
		ResDTO res = null;
		Long writerId = req.getWriterId();
		String content = req.getContent();
		try {
			Optional<Users> writer = userRepository.findById(writerId);
			if(writer.isPresent()) {
				Post post = new Post(writer.get(),content);
				//포스트 이미지가 존재할경우
				if(req.getImages() !=null) {
					//포스트 이미지 관리 컴포넌트 호출
					List<PostImage> postImages = postImageManager.saveImage(post, req.getImages());
					post.addImgs(postImages);
				}
				Post testPost = postRepository.save(post);
				postImageManager.getImage(testPost);
				res = new ResDTO(HttpStatus.CREATED,"성공");
			}else {
				res = new ResDTO(HttpStatus.NOT_FOUND,"회원 정보 찾을 수 없음"); 
			}
		}catch(Exception e) {
			res = new ResDTO(HttpStatus.CONFLICT,"서버 내부오류");
			e.printStackTrace();
		}finally
		{
			return res;
		}
	}
	//유저아이디로 Post조회
	@Transactional(readOnly = true)
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
					//덧글수
					Long commentCount = postCommentRepository.countByPostNo(postDTO.getNo());
					postDTO.setCommentCount(commentCount);
					String writerNickname = userRepository.findById(postDTO.getUsersNo()).get().getNickname();
					postDTO.setWriterNickname(writerNickname);
					Post post = postRepository.findById(postDTO.getNo()).get();
					List<String> imgs = postImageManager.getImage(post);
					postDTO.setImg(imgs);
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
	//Post추천
	@Transactional
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
	//comment 작성
	@Transactional
	public CommentWriteResDTO writeComment(Long writerNo,Long postNo ,String content) {
		CommentWriteResDTO res = null;
		try {
			Optional<Users> optionalWriter = userRepository.findById(writerNo);
			Optional<Post> optionalPost = postRepository.findById(postNo);
			if(optionalWriter.isPresent() && optionalPost.isPresent()) {
				Users user = optionalWriter.get();
				Post post = optionalPost.get();
				//testing
				PostComment postComment = new PostComment(user,post,content);
				post.addComment(postComment);
				postCommentRepository.save(postComment);
				postRepository.save(post);
				Long commentCount = (long)postRepository.findById(postNo).get().getComments().size();
				res = new CommentWriteResDTO(HttpStatus.CREATED,"작성 성공",commentCount);
			}else if(optionalWriter.isEmpty()){
				res = new CommentWriteResDTO(HttpStatus.BAD_REQUEST,"잘못된 사용자",null);
			}else {
				res = new CommentWriteResDTO(HttpStatus.BAD_REQUEST,"잘못된 계시글 접근",null);
			}
		}catch(Exception e) {
			res = new CommentWriteResDTO(HttpStatus.CONFLICT,e.toString(),null);
		}finally {
			return res;
		}
	}
	//PostNo로 작성된 comment 조회
	@Transactional(readOnly = true)
	public PostCommentResDTO getComment(Long postNo) {
		PostCommentResDTO res = null;
		try {
			Optional<Post> optionalPost = postRepository.findById(postNo);
			if(optionalPost.isPresent()) {
				Post post = optionalPost.get();
				List<PostComment> comments = post.getComments();
				res = new PostCommentResDTO(HttpStatus.OK, postNo+"번 comment 조회",comments);
			}else {
				res = new PostCommentResDTO(HttpStatus.NOT_FOUND,"계시글이 존재하지 않습니다.",null);
			}
		}catch(Exception e) {
			res = (PostCommentResDTO) new ResDTO(HttpStatus.CONFLICT,e.toString());
		}finally {
			return res;
		}
	}
	//Post 삭제
	@Transactional
	public HttpStatus deletePost(Long postNo , Long userId) {
		Post post = postRepository.findById(postNo)
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "계시글을 찾을 수 없습니다."));
		if(post.getUsers().getNo() == userId) {
			postRepository.delete(post);
		}else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글을 삭제할 권한이 없습니다.");
		}
		return HttpStatus.OK;
	}
	//comment삭제
	@Transactional
	public HttpStatus deleteComment(Long commentNo, Long userId) {
		PostComment comment = postCommentRepository.findById(commentNo)
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Comment를 찾을 수 없습니다."));
		if(comment.getUsers().getNo() == userId) {
			postCommentRepository.delete(comment);
		}else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Comment를 삭제할 권한이 없습니다.");
		}
		return HttpStatus.OK;
	}
}