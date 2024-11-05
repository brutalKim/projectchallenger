package site.challenger.project_challenger.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
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
import site.challenger.project_challenger.constants.Common;
import site.challenger.project_challenger.domain.Challenge;
import site.challenger.project_challenger.domain.ChallengeHasPost;
import site.challenger.project_challenger.domain.CommentRecommend;
import site.challenger.project_challenger.domain.Follow;

import site.challenger.project_challenger.domain.Notice;

import site.challenger.project_challenger.domain.LocationRef;

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
import site.challenger.project_challenger.repository.CommentRecommendRepository;
import site.challenger.project_challenger.repository.FollowRepository;
import site.challenger.project_challenger.repository.NoticeRepository;
import site.challenger.project_challenger.repository.PostCommentRepository;
import site.challenger.project_challenger.repository.PostRecommendRepository;
import site.challenger.project_challenger.repository.PostRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.util.InsuUtils;
import site.challenger.project_challenger.util.PostImageManager;

/*
 * 
 * 
 * 포스트리턴시 포스트 전처리 메서드를 꼭 이용할 것
 * postPreprocessing 는 맨 밑에 존재
 * 
 * 
 * */
@RequiredArgsConstructor
@Service
public class PostManagementService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostRecommendRepository postRecommendRepository;
	private final PostCommentRepository postCommentRepository;
	private final ChallengeRepository challengeRepository;
	private final ChallengeHasPostRepository challengeHasPostRepository;
	private final FollowRepository followRepository;
	// 포스트 이미지 관리 컴포넌트
	private final PostImageManager postImageManager;
	private final NoticeRepository noticeRepository;
	private final CommentRecommendRepository commentRecommendRepository;
	// Post작성

	@Transactional
	public CommonResponseDTO writePost(PostWriteServiceReqDTO req) {
		CommonResponseDTO res = null;
		Long writerId = req.getWriterId();
		String content = req.getContent();
		List<Long> tagChallenges = req.getTagChallenges();
		try {
			Optional<Users> writer = userRepository.findById(writerId);
			if (writer.isPresent()) {
				Post post = new Post(writer.get(), content);
				// 포스트 이미지가 존재할경우
				if (req.getImages() != null) {
					// 포스트 이미지 관리 컴포넌트 호출
					List<PostImage> postImages = postImageManager.saveImage(post, req.getImages());
					post.addImgs(postImages);
				}
				Post savedPost = postRepository.save(post);
				// 챌린지를 태그 할 경우
				if (tagChallenges != null) {
					for (Long challengeNo : tagChallenges) {
						Optional<Challenge> optionalChallenge = challengeRepository.findById(challengeNo);
						if (optionalChallenge.isPresent()) {
							Challenge challenge = optionalChallenge.get();
							ChallengeHasPost CHP = new ChallengeHasPost(challenge, savedPost);

							challengeHasPostRepository.save(CHP);

							challenge.getChallengeHasPost().add(CHP);
							// 저장
							challengeRepository.save(challenge);
							savedPost.getChallengeHasPost().add(CHP);
							postRepository.save(savedPost);
						}
					}
				}
				// TODO : 이후 조회하고있는 포스트에 맞게 응답할것
				res = new CommonResponseDTO(HttpStatus.CREATED);
			} else {
				res = new CommonResponseDTO(HttpStatus.NOT_FOUND, "존재하지 않는 작성자");
			}
		} catch (Exception e) {
			res = new CommonResponseDTO(HttpStatus.CONFLICT, e.toString());
			e.printStackTrace();
		} finally {
			return res;
		}
	}

	// 추천 포스트
	@Transactional(readOnly = true)
	public CommonResponseDTO getRecommendPost(Long userNo, int page) {
		Pageable pageable = PageRequest.of(page, 10);
		Map<String, Object> map = new HashMap<>();
		Page<PostDTO> postDTOs = postRepository.getRecommendPost(userNo, pageable);
		// 포스트 전처리
		List<PostDTO> postDTOList = postPreprocessing(postDTOs.getContent());
		map.put("data", postDTOList );
		map.put("nextPage",postDTOs.hasNext());
		return new CommonResponseDTO(map, HttpStatus.OK);
	}

	// 팔로우 기반 Post 조회
	@Transactional(readOnly = true)
	public CommonResponseDTO getPostByFollow(Long userNo, int page) {
		ArrayList<Follow> follows = followRepository.getFollow(userNo);
		List<Long> followsNo = new ArrayList<>();
		for (Follow f : follows) {
			followsNo.add(f.getFollowUsers().getNo());
		}
		return getByUserId(followsNo, userNo, page);
	}

	// 지역 기반 post 조회
	@Transactional(readOnly = true)
	public CommonResponseDTO getPostByRegion(Long userNo, int page) {
		Optional<Users> user = userRepository.findById(userNo);
		if(user.isPresent()) {
			LocationRef locationRef = user.get().getLocationRef();
			Pageable pageable = PageRequest.of(page, 10);
			List<PostDTO> postDTOs = postRepository.findAllByLocationRef(locationRef, userNo,pageable).getContent();
			Map map = new HashMap<>();
			map.put("data", postPreprocessing(postDTOs));
			return new CommonResponseDTO(map,HttpStatus.OK);
		}
		return new CommonResponseDTO(HttpStatus.BAD_REQUEST);
	}

	// 유저아이디로 Post조회
	@Transactional(readOnly = true)
	public CommonResponseDTO getByUserId(List<Long> writerNo, Long userNo, int page) {
		CommonResponseDTO res = null;
		try {
			// 존재하는 유저인지
			Boolean areAllUsersExists = userRepository.countExistingUser(writerNo) == writerNo.size();
			Optional<Users> optionalUser = userRepository.findById(userNo);
			if (areAllUsersExists && optionalUser.isPresent()) {
				Users user = optionalUser.get();
				Pageable pageable = PageRequest.of(page, 10);
				Page<PostDTO> postsPage = postRepository.getPostByWriterAndUser(writerNo, user.getNo(), pageable);
				ArrayList<PostDTO> postsArrayList = postPreprocessing(postsPage.getContent());
				
				if (postsPage.isEmpty()) {
					res = new CommonResponseDTO(HttpStatus.NOT_FOUND, "작성한 글을 찾을 수 없습니다.");
				} else {
					Map<String, Object> map = new HashMap<>();
					map.put("data", postsArrayList);
					map.put("nextPage", postsPage.hasNext());
					res = new CommonResponseDTO(map, HttpStatus.OK);
				}
			} else {
				res = new CommonResponseDTO(HttpStatus.UNAUTHORIZED, "유효하지 않은 작성자");
			}
		} catch (Exception e) {

			e.printStackTrace();

			res = new CommonResponseDTO(HttpStatus.CONFLICT, e.toString());
		} finally {
			return res;
		}
	}

	// 키워드로 포스트 검색
	@Transactional(readOnly = true)
	public CommonResponseDTO getByKeyWord(Long userNo, int page, String keyWord) {
		CommonResponseDTO res = null;
		Map<String, Object> map = new HashMap<>();
		if(keyWord.isEmpty()) {
			map.put("data",new ArrayList<>());
			map.put("nextPage", false);
			return new CommonResponseDTO(map,HttpStatus.OK);
		}
		try {
			Pageable pageable = PageRequest.of(page, 10);
			Page<PostDTO> postsPage = postRepository.getPostByKeyword(keyWord, userNo, pageable);
			ArrayList<PostDTO> postDTOs = postPreprocessing(postsPage.getContent());
			map.put("data", postDTOs);
			map.put("nextPage", postsPage.hasNext());
			res = new CommonResponseDTO(map, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			res = new CommonResponseDTO(HttpStatus.CONFLICT, e.toString());
		} finally {
			return res;
		}
	}

	// Post추천
	@Transactional
	public CommonResponseDTO recommend(PostRecommendServiceReqDTO req) {
		CommonResponseDTO res = null;
		Long userId = req.getUserId();
		Long postNo = req.getPostNo();
		try {
			Optional<Users> optionalRecommender = userRepository.findById(userId);
			Optional<Post> optionalPost = postRepository.findById(postNo);
			Map<String, Object> map = new HashMap<>();
			// 사용자와 포스트가 존재할때만
			if (optionalRecommender.isPresent() && optionalPost.isPresent()) {
				PostRecommend postRecommend = new PostRecommend(optionalRecommender.get(), optionalPost.get());
				Optional<PostRecommend> optionalPostRecommend = postRecommendRepository.findById(postRecommend.getPK());
				// 추천한 이력이 없을시 추천
				if (optionalPostRecommend.isEmpty()) {
					PostRecommend save = postRecommendRepository.save(postRecommend);
					Post post = optionalPost.get();
					post.incrementRecommend();
					postRepository.save(post);
					map.put("type", "recommend");
					map.put("recommendCount", post.getRecommend());
					res = new CommonResponseDTO(map, HttpStatus.OK);
					// notice 추가
					Notice notice = new Notice();
					notice.setKind(Common.SOMEONE_LIKE_POST);
					notice.setSentUsers(optionalRecommender.get());
					notice.setTargetusers(post.getUsers());
					notice.setTargetno(post.getNo());
					noticeRepository.save(notice);
				} else {
					// 추천한 이력이 있을시 비추
					PostRecommend existedRecommend = optionalPostRecommend.get();
					postRecommendRepository.deleteById(existedRecommend.getPK());
					Post post = optionalPost.get();
					post.decrementRecommend();
					map.put("type", "unrecommend");
					map.put("recommendCount", post.getRecommend());
					res = new CommonResponseDTO(map, HttpStatus.OK);
				}
				// 추천자 or 추천할 포스트가 없을 경우
			} else {
				res = new CommonResponseDTO(HttpStatus.BAD_REQUEST, "잘못된 요청");
			}
		} catch (Exception e) {
			res = new CommonResponseDTO(HttpStatus.CONFLICT, e.toString());
		} finally {
			return res;
		}
	}

	// comment 작성
	@Transactional
	public CommonResponseDTO writeComment(Long writerNo, Long postNo, String content) {
		CommonResponseDTO res = null;
		Map<String, Object> map = new HashMap<>();
		try {
			Optional<Users> optionalWriter = userRepository.findById(writerNo);
			Optional<Post> optionalPost = postRepository.findById(postNo);
			if (optionalWriter.isPresent() && optionalPost.isPresent()) {
				Users user = optionalWriter.get();
				Post post = optionalPost.get();
				// testing
				PostComment postComment = new PostComment(user, post, content);
				post.addComment(postComment);
				postCommentRepository.save(postComment);
				postRepository.save(post);
				Long commentCount = (long) postRepository.findById(postNo).get().getComments().size();
				// commentCount
				map.put("commentCount", commentCount);
				res = new CommonResponseDTO(map, HttpStatus.OK);
				// notice 추가
				Notice notice = new Notice();
				notice.setKind(Common.SOMEONE_COMMENT_POST);
				notice.setSentUsers(optionalWriter.get());
				notice.setTargetusers(post.getUsers());
				notice.setTargetno(post.getNo());
				noticeRepository.save(notice);

				// 작성자가 존재하지 않을 경우
			} else if (optionalWriter.isEmpty()) {
				res = new CommonResponseDTO(HttpStatus.UNAUTHORIZED, "존재하지 않은 작성자");
				// 포스트가 존재하지 않을 경우
			} else {
				res = new CommonResponseDTO(HttpStatus.NOT_FOUND, "존재하지 않은 계시글");
			}
		} catch (Exception e) {
			res = new CommonResponseDTO(HttpStatus.CONFLICT, e.toString());
		} finally {
			return res;
		}
	}

	// PostNo로 작성된 comment 조회
	@Transactional(readOnly = true)
	public CommonResponseDTO getComment(Long userNo, Long postNo) {
		CommonResponseDTO res = null;
		Map<String, Object> map = new HashMap<>();
		try {
			Optional<Post> optionalPost = postRepository.findById(postNo);
			if (optionalPost.isPresent()) {
				Post post = optionalPost.get();
				List<Comment> commentArrayList = new ArrayList<>();
				List<PostComment> comments = post.getComments();

				Users user = userRepository.getById(userNo);

				//TODO:포스트 조회시 이미 추천한 포스트인지 아닌지가 안됨 아마 post.getComments에서 가져오면 안될듯함
				for(PostComment comment : comments) {
					boolean recommended = commentRecommendRepository.existsByPostCommentAndRecommendUsers(comment.getNo(), userNo);
					commentArrayList.add(new Comment(comment.getNo(),comment.getUsers().getNickname(),comment.getUsers().getNo(),comment.getRecommend(),comment.getContent(),recommended));


				}
				map.put("comments", commentArrayList);
				// legacy Code
				// res = new PostCommentResDTO(HttpStatus.OK, postNo+"번 comment 조회",comments);
				res = new CommonResponseDTO(map, HttpStatus.OK, postNo + "번 comment 조회", null, true);
			} else {
				res = new CommonResponseDTO(HttpStatus.NOT_FOUND, "포스트가 존재하지 않습니다.");
			}
		} catch (Exception e) {
			// = (PostCommentResDTO) new ResDTO(HttpStatus.CONFLICT,e.toString());
			res = new CommonResponseDTO(HttpStatus.CONFLICT, e.toString());
		} finally {
			return res;
		}
	}

	// comment 추천
	@Transactional
	public CommonResponseDTO recommendComment(Long userNo, Long commentNo) {
		Optional<Users> user = userRepository.findById(userNo);
		Optional<PostComment> targetComment = postCommentRepository.findById(commentNo);
		if (user.isPresent() && targetComment.isPresent()) {
			Optional<CommentRecommend> recommendComment = commentRecommendRepository
					.findByPostCommentAndRecommendUsers(targetComment.get(), user.get());
			// 유저가 타겟코멘트를 추천했을 경우
			if (recommendComment.isPresent()) {
				// 연관관게삭제
				commentRecommendRepository.delete(recommendComment.get());
				// DTO생성
				Long recommend = targetComment.get().getRecommend();
				Map<String, Object> map = new HashMap<>();
				map.put("recommend", false);
				map.put("recommendCount", recommend);
				return new CommonResponseDTO(map, HttpStatus.ACCEPTED);
			}
			// 유저가 타겟코멘트를 추천하지 않았을 경우
			// 연관관계생성
			if (recommendComment.isEmpty()) {
				CommentRecommend commentRecommend = new CommentRecommend(targetComment.get(), user.get());
				commentRecommendRepository.save(commentRecommend);
				Map<String, Object> map = new HashMap<>();
				map.put("recommend", true);
				Long recommendCount = targetComment.get().getRecommend();
				map.put("recommendCount", recommendCount);
				// notice 추가
				Notice notice = new Notice();
				notice.setKind(Common.SOMEON_LIKE_COMMENT);
				notice.setSentUsers(user.get());
				notice.setTargetusers(targetComment.get().getUsers());
				notice.setTargetno(targetComment.get().getNo());
				notice.setTargetmasterno(targetComment.get().getPost().getNo());
				noticeRepository.save(notice);

				return new CommonResponseDTO(map, HttpStatus.ACCEPTED);
			}
		}
		// 잘못된 요청 시 리턴
		return new CommonResponseDTO(HttpStatus.BAD_REQUEST);
	}

	// 삭제는 아직 테스트 and 구현 안해봤음 테스트 or 구현 이후 CommonResponseDTO로 return value 수정할 예정
	// Post 삭제

	@Transactional
	public HttpStatus deletePost(Long postNo, Long userId) {
		Post post = postRepository.findById(postNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "계시글을 찾을 수 없습니다."));
		if (post.getUsers().getNo() == userId) {
			postRepository.delete(post);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글을 삭제할 권한이 없습니다.");
		}
		return HttpStatus.OK;
	}

	// comment삭제
	@Transactional
	public HttpStatus deleteComment(Long commentNo, Long userId) {
		PostComment comment = postCommentRepository.findById(commentNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment를 찾을 수 없습니다."));
		if (comment.getUsers().getNo() == userId) {
			postCommentRepository.delete(comment);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Comment를 삭제할 권한이 없습니다.");
		}
		return HttpStatus.OK;
	}

	// 코맨트 inner Class 전체적 코드를 유지하기 위해
	@Getter
	@Setter
	@AllArgsConstructor
	public class Comment {
		private Long CommentNo;
		private String nickname;
		private Long userId;
		private Long recommend;
		private String Content;
		private boolean isRecommended;
	}
	@Getter
	@Setter
	@AllArgsConstructor
	public class page{
		
	}
	// 포스트 DTO 전처리
	public ArrayList<PostDTO> postPreprocessing(List<PostDTO> prePostData) {
		ArrayList<PostDTO> data = new ArrayList<>();
		for (PostDTO postDTO : prePostData) {
			// 덧글수
			Long commentCount = postCommentRepository.countByPostNo(postDTO.getNo());
			postDTO.setCommentCount(commentCount);
			// 닉네임
			Users writer = userRepository.findById(postDTO.getUsersNo()).get();
			String writerNickname = writer.getNickname();
			String profileImg = writer.getProfile().getSavedName();
			if (profileImg == null) {
				profileImg = "defaultImg";
			} else {
				profileImg = "/userProfileImg/" + profileImg;
			}
			postDTO.setProfileImg(profileImg);
			postDTO.setWriterNickname(writerNickname);
			// 이미지
			Post post = postRepository.findById(postDTO.getNo()).get();
			List<String> imgs = postImageManager.getImage(post);
			postDTO.setImg(imgs);

			ArrayList<ChallengeHasPost> chps = challengeHasPostRepository.findByPostNo(post.getNo());
			for (ChallengeHasPost chp : chps) {
				Optional<Challenge> challenge = challengeRepository
						.findActiveById(chp.getChallengeHasPostPrimaryKey().getChallengeNo());
				if (challenge.isPresent()) {
					String title = challenge.get().getTitle();
					postDTO.addTaggedChallenge(title, chp.getChallengeHasPostPrimaryKey().getChallengeNo());
				}

			}
			data.add(postDTO);
		}
		return data;
	}
}