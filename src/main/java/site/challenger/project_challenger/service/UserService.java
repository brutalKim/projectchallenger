package site.challenger.project_challenger.service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.Follow;
import site.challenger.project_challenger.domain.Profile;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.user.SearchUsersDTO;
import site.challenger.project_challenger.dto.user.UserRequestDTO;
import site.challenger.project_challenger.repository.FollowRepository;
import site.challenger.project_challenger.repository.PostRepository;
import site.challenger.project_challenger.repository.ProfileRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.util.FileSaver;
import site.challenger.project_challenger.util.InsuUtils;

@Service
@RequiredArgsConstructor
public class UserService {
	private final FileSaver fileSaver;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final FollowRepository followRepository;
	private final ProfileRepository profileRepository;

	// 이미지 지우기

	public CommonResponseDTO deleteProfileImage(long requestUserNo) {
		Users requestUser = userRepository.findById(requestUserNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 유저"));
		Profile requestUserProfile = requestUser.getProfile();
		requestUserProfile.setOriginalName(null);
		requestUserProfile.setSavedName(null);
		userRepository.save(requestUser);

		CommonResponseDTO response = new CommonResponseDTO(HttpStatus.OK, true);

		return response;
	}

	// 이미지 변경
	public String changeProfileImage(HttpServletRequest request, MultipartFile file, long requestUserNo) {

		String imageType = file.getContentType();
		if (!(imageType.contains("png") || imageType.contains("jpg") || imageType.contains("jpeg"))) {
			throw InsuUtils.throwNewResponseStatusException(HttpStatus.FORBIDDEN, "허용된 이미지 파일이 아님");
		}

		try (var inputStream = file.getInputStream()) {

			BufferedImage bufferedImage = ImageIO.read(inputStream);
			int width = bufferedImage.getWidth();
			int height = bufferedImage.getHeight();
			if (width != height) {
				System.out.println("###########################");
				System.out.println(width);
				System.out.println(height);
				System.out.println("###########################");
				throw InsuUtils.throwNewResponseStatusException(HttpStatus.BAD_REQUEST, "가로 세로가 일치하지 않음");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw InsuUtils.throwNewResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 이미지가 아님");
		}

		return fileSaver.saveFileForProfile(file, request, requestUserNo);
	}

	// 유저 정보조회
	public CommonResponseDTO getUserDetail(Long userNo, Long targetNo) {
		Long postCount = postRepository.getPostCount(targetNo);
		boolean isFollowed = followRepository.existsByUserNoAndTargetUserNo(userNo, targetNo);

		ArrayList<FollowDTO> follows = preprocessingFollow(followRepository.getFollow(targetNo));
		ArrayList<FollowDTO> followers = preprocessingFollow(followRepository.getFollower(targetNo));
		Map<String, Object> map = new HashMap<>();
		map.put("postCount", postCount);
		map.put("isFollowed", isFollowed);
		map.put("Follow", follows);
		map.put("Follower", followers);
		return new CommonResponseDTO(map, HttpStatus.OK);
	}

	// insu 1028 for follow, followers
	public CommonResponseDTO getUserDetailF(long requestUserNo, long[] targetUserNoArray) {

		Map<String, Object> body = new HashMap<>();
		List<TargetUserDetail> targetUserList = new ArrayList<>();
		for (long targetUserNo : targetUserNoArray) {
			Users targetUser = userRepository.findById(targetUserNo)
					.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("해당 유저는 존재하지 않음"));
			Long postCount = postRepository.getPostCount(targetUserNo);

			boolean isFollowed = followRepository.existsByUserNoAndTargetUserNo(requestUserNo, targetUserNo);
			String userProfileImage = profileRepository.findByUser(targetUser)
					.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("타겟 유저의 프로파일이 존재하지 않음"))
					.getSavedName();
			ArrayList<FollowDTO> follows = preprocessingFollow(followRepository.getFollow(targetUserNo));
			ArrayList<FollowDTO> followers = preprocessingFollow(followRepository.getFollower(targetUserNo));

			TargetUserDetail targetUserDetail = new TargetUserDetail(targetUser.getNickname(), targetUser.getNo(),
					isFollowed, userProfileImage, follows, followers, postCount);
			targetUserList.add(targetUserDetail);
		}
		body.put("targetUserList", targetUserList);

		return new CommonResponseDTO(body, HttpStatus.OK);
	}

	// 유저 팔로우
	public CommonResponseDTO followUser(Long userNo, Long targetUserNo) {
		boolean isExistFollow = followRepository.existsByUserNoAndTargetUserNo(userNo, targetUserNo);
		Map map = new HashMap<>();
		if (isExistFollow) {
			Follow deleteFollow = followRepository.getFollow(userNo, targetUserNo);
			followRepository.delete(deleteFollow);
			ArrayList<FollowDTO> follows = preprocessingFollow(followRepository.getFollower(targetUserNo));

			map.put("Follower", follows);
			map.put("type", "unfollow");
			return new CommonResponseDTO(map, HttpStatus.ACCEPTED);
		}
		Users user = userRepository.getById(userNo);
		Users targetUser = userRepository.getById(targetUserNo);
		Follow newFollow = new Follow(user, targetUser);
		followRepository.save(newFollow);
		ArrayList<FollowDTO> follows = preprocessingFollow(followRepository.getFollower(targetUserNo));

		map.put("Follower", follows);
		map.put("type", "follow");
		return new CommonResponseDTO(map, HttpStatus.ACCEPTED);

	}

	public CommonResponseDTO changeUserDetail(long requestUserNo, UserRequestDTO userRequestDTO) {
		Users requestUser = userRepository.findById(requestUserNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 유저"));

		String userNickName = userRequestDTO.getUserNickName();
		String userDescription = userRequestDTO.getUserDescription();
		// 동일한 닉네임이 있는지 확인
		boolean isExist = userRepository.existsByNickname(userNickName);

		if (isExist) {
			InsuUtils.throwNewResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 유저 닉네임");
		}

		requestUser.setNickname(userNickName);
		requestUser.getProfile().setDescription(userDescription);

		Users savedUser = userRepository.save(requestUser);

		Map<String, Object> body = new HashMap<String, Object>();

		body.put("userNickName", savedUser.getNickname());
		body.put("userDescription", savedUser.getProfile().getDescription());

		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK);

		return response;
	}

	// 해당 닉네임을 가진 유저가 존재하는지
	public CommonResponseDTO existsByNickName(String nickName) {
		boolean isExist = userRepository.existsByNickname(nickName);

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("isExist", isExist);
		CommonResponseDTO response = new CommonResponseDTO(body, HttpStatus.OK);

		return response;
	}

	// 닉네임을 키워드로하는 유저 조회
	public CommonResponseDTO getUserBykeyWord(Long userNo, String keyWord) {
		ArrayList<SearchUsersDTO> userDTOs = userRepository.searchUserByKeyWord(userNo, keyWord);
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("result", userDTOs);
		return new CommonResponseDTO(body, HttpStatus.OK);
	}

	// 팔로우 팔로워 전처리
	private ArrayList<FollowDTO> preprocessingFollow(ArrayList<Follow> follows) {
		ArrayList<FollowDTO> userList = new ArrayList<>();
		for (Follow f : follows) {
			userList.add(new FollowDTO(f.getUsers().getNickname(), f.getFollowUsers().getNo()));
		}
		return userList;
	}

	// 닉네임과 유저정포 페어 DTO 여기서만 사용될거같아서 private innerClass로 생성
	@Getter
	@Setter
	private class FollowDTO {
		private String nickname;
		private Long userNo;

		public FollowDTO(String nickname, Long userNo) {
			this.nickname = nickname;
			this.userNo = userNo;
		}
	}

	// getUserDeatailF 용 1028 insu
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	private class TargetUserDetail {
		private String nickname;
		private long userNo;
		private boolean isFollowed;
		private String userProfileImage;
		private ArrayList<FollowDTO> follows;
		private ArrayList<FollowDTO> followers;
		private long postCount;

	}
}
