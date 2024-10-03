package site.challenger.project_challenger.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.ChallengeHasPost;
import site.challenger.project_challenger.domain.Post;
import site.challenger.project_challenger.domain.PostComment;
import site.challenger.project_challenger.domain.PostImage;
import site.challenger.project_challenger.domain.PostRecommend;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.post.PostDTO;
import site.challenger.project_challenger.dto.post.PostRecommendServiceReqDTO;
import site.challenger.project_challenger.dto.post.PostWriteServiceReqDTO;
import site.challenger.project_challenger.repository.ChallengeHasPostRepository;
import site.challenger.project_challenger.repository.ChallengeRepository;
import site.challenger.project_challenger.repository.PostCommentRepository;
import site.challenger.project_challenger.repository.PostRecommendRepository;
import site.challenger.project_challenger.repository.PostRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.util.PostImageManager;

@RequiredArgsConstructor
@Service
public class PostManagementService {
	private final PostRepository postRepository;
	private final UserRepository userRepository; 
	private final PostRecommendRepository postRecommendRepository;
	private final PostCommentRepository postCommentRepository;
	private final ChallengeRepository challengeRepository;
	private final ChallengeHasPostRepository challengeHasPostRepository;
	//포스트 이미지 관리 컴포넌트
	private final PostImageManager postImageManager;
	
	//Post작성
	@Transactional
	public CommonResponseDTO writePost(PostWriteServiceReqDTO req) {
		CommonResponseDTO res = null;
		Long writerId = req.getWriterId();
		String content = req.getContent();
		List<Long> tagChallenges = req.getTagChallenges();
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
				Post savedPost = postRepository.save(post);
				//챌린지를 태그 할 경우
				if(tagChallenges != null) {
					for(Long challengeNo : tagChallenges) {
						Optional<Challenge> optionalChallenge = challengeRepository.findById(challengeNo);
						if(optionalChallenge.isPresent()) {
							Challenge challenge = optionalChallenge.get();
							ChallengeHasPost CHP = new ChallengeHasPost(challenge,savedPost);
							challenge.getChallengeHasPost().add(CHP);
							//저장
							challengeRepository.save(challenge);
							challengeHasPostRepository.save(CHP);
							savedPost.getChallengeHasPost().add(CHP);
							postRepository.save(savedPost);
						}
					}
				}
				res = new CommonResponseDTO(HttpStatus.CREATED);
			}else {
				res = new CommonResponseDTO(HttpStatus.NOT_FOUND,"존재하지 않는 작성자");
			}
		}catch(Exception e) {
			res = new CommonResponseDTO(HttpStatus.CONFLICT,e.toString());
			e.printStackTrace();
		}finally
		{
			return res;
		}
	}
	//추천 포스트 
	@Transactional (readOnly = true)
	public CommonResponseDTO getRecommendPost(Long userNo, int page) {
		Pageable pageable = PageRequest.of(page, 10);
		ArrayList<PostDTO> postDTOs = (ArrayList<PostDTO>) postRepository.getRecommendPost(userNo, pageable).getContent();
		Map<String,Object> map = new HashMap<>();
		map.put("posts",(Object)postDTOs);
		return new CommonResponseDTO(map,HttpStatus.OK);
	}
	//유저아이디로 Post조회
	@Transactional(readOnly = true)
	public CommonResponseDTO getByUserId(Long writerNo ,Long userNo, int page) {
		CommonResponseDTO res = null;
		System.out.println(writerNo + " : " + userNo + " : " + page);
		try {
			Optional<Users>optionalWriter = userRepository.findById(writerNo);
			Optional<Users>optionalUser = userRepository.findById(userNo);
			if(optionalWriter.isPresent() && optionalUser.isPresent()) {
				Users writer = optionalWriter.get();
				Users user = optionalUser.get();
				Pageable pageable = PageRequest.of(page, 10);
				List<PostDTO> postsArray = postRepository.getPostByWriterAndUser(writer.getNo(),user.getNo(),pageable).getContent();
				//포스트 전처리
				for(PostDTO post : postsArray) {
					System.out.println(post.getNo());
				}
				postsArray = postPreprocessing(postsArray);
				if(postsArray.size() == 0) {
					res = new CommonResponseDTO(HttpStatus.NOT_FOUND,writer.getNickname()+"이 작성한 글을 찾을 수 없습니다.");
				}else {
					Map<String,Object> map = new HashMap<>();
					map.put("posts", (Object) postsArray);
					res = new CommonResponseDTO(map,HttpStatus.OK);
				}
			}else {
				res = new CommonResponseDTO(HttpStatus.UNAUTHORIZED,"유효하지 않은 작성자");
			}
		}catch(Exception e) {

			e.printStackTrace();
			
			res = new CommonResponseDTO(HttpStatus.CONFLICT,e.toString());
		}finally {
			return res;
		}
	}
	//Post추천
	@Transactional
	public CommonResponseDTO recommend(PostRecommendServiceReqDTO req) {
		CommonResponseDTO res = null;
		Long userId = req.getUserId();
		Long postNo = req.getPostNo();
		try {
			Optional<Users> optionalRecommender = userRepository.findById(userId);
			Optional<Post> optionalPost = postRepository.findById(postNo);
			Map<String,Object> map = new HashMap<>();
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
					map.put("type", "recommend");
					map.put("recommendCount",post.getRecommend());
					res = new CommonResponseDTO(map,HttpStatus.OK);
				}else {
					//추천한 이력이 있을시 비추
					PostRecommend existedRecommend = optionalPostRecommend.get();
					postRecommendRepository.deleteById(existedRecommend.getPK());
					Post post = optionalPost.get();
					post.decrementRecommend();
					map.put("type", "unrecommend");
					map.put("recommendCount",post.getRecommend());
					res = new CommonResponseDTO(map,HttpStatus.OK);
				}
				//추천자 or 추천할 포스트가 없을 경우
			}else {
				res = new CommonResponseDTO(HttpStatus.BAD_REQUEST,"잘못된 요청");
			}
		}catch(Exception e) {
			res = new CommonResponseDTO(HttpStatus.CONFLICT,e.toString());
		}finally {
			return res;
		}
	}
	//comment 작성
	@Transactional
	public CommonResponseDTO writeComment(Long writerNo,Long postNo ,String content) {
		CommonResponseDTO res = null;
		Map<String,Object> map = new HashMap<>();
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
				//commentCount
				map.put("commentCount", commentCount);
				res = new CommonResponseDTO(map,HttpStatus.OK);
				//작성자가 존재하지 않을 경우
			}else if(optionalWriter.isEmpty()){
				res = new CommonResponseDTO(HttpStatus.UNAUTHORIZED,"존재하지 않은 작성자");
				//포스트가 존재하지 않을 경우
			}else {
				res = new CommonResponseDTO(HttpStatus.NOT_FOUND,"존재하지 않은 계시글");
			}
		}catch(Exception e) {
			res = new CommonResponseDTO(HttpStatus.CONFLICT,e.toString());
		}finally {
			return res;
		}
	}
	//PostNo로 작성된 comment 조회
	@Transactional(readOnly = true)
	public CommonResponseDTO getComment(Long postNo) {
		CommonResponseDTO res = null;
		Map<String,Object> map = new HashMap<>();
		try {
			Optional<Post> optionalPost = postRepository.findById(postNo);
			if(optionalPost.isPresent()) {
				Post post = optionalPost.get();
				List <Comment> commentArrayList = new ArrayList<>();
				List<PostComment> comments = post.getComments();
				for(PostComment comment : comments) {
					commentArrayList.add(new Comment(comment.getNo(),comment.getUsers().getNickname(),comment.getUsers().getNo(),comment.getContent()));
				}
				map.put("comments", commentArrayList);
				//legacy Code
				//res = new PostCommentResDTO(HttpStatus.OK, postNo+"번 comment 조회",comments);
				res = new CommonResponseDTO(map,HttpStatus.OK,postNo+"번 comment 조회",null, true);
			}else {
				res = new CommonResponseDTO(HttpStatus.NOT_FOUND,"포스트가 존재하지 않습니다.");
			}
		}catch(Exception e) {
			// = (PostCommentResDTO) new ResDTO(HttpStatus.CONFLICT,e.toString());
			res = new CommonResponseDTO(HttpStatus.CONFLICT,e.toString());
		}finally {
			return res;
		}
	}
	//삭제는 아직 테스트 and 구현 안해봤음 테스트 or 구현 이후 CommonResponseDTO로 return value 수정할 예정
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
	//코맨트 inner Class 전체적 코드를 유지하기 위해
	@Getter
	@Setter
	@AllArgsConstructor
	public class Comment{
		private Long CommentNo;
		private String nickname;
		private Long userId;
		private String Content;
	}
	//포스트 DTO 전처리
	private ArrayList<PostDTO> postPreprocessing(List<PostDTO> prePostData){
		ArrayList<PostDTO> data = new ArrayList<>();
		for(PostDTO postDTO : prePostData) {
			//덧글수
			Long commentCount = postCommentRepository.countByPostNo(postDTO.getNo());
			postDTO.setCommentCount(commentCount);
			//닉네임
			String writerNickname = userRepository.findById(postDTO.getUsersNo()).get().getNickname();
			postDTO.setWriterNickname(writerNickname);
			//이미지
			Post post = postRepository.findById(postDTO.getNo()).get();
			List<String> imgs = postImageManager.getImage(post);
			postDTO.setImg(imgs);
			data.add(postDTO);
		}
		return data;
	}
}