package site.challenger.project_challenger.service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.challenger.project_challenger.domain.Follow;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.repository.FollowRepository;
import site.challenger.project_challenger.repository.PostRepository;
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
				throw InsuUtils.throwNewResponseStatusException(HttpStatus.BAD_REQUEST, "가로 세로가 일치하지 않음");
			}

		} catch (Exception e) {
			throw InsuUtils.throwNewResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 이미지가 아님");
		}

		return fileSaver.saveFileForProfile(file, request, requestUserNo);
	}
	//유저 정보조회
	public CommonResponseDTO getUserDetail(Long userNo,Long targetNo) {
		Long postCount = postRepository.getPostCount(targetNo);
		boolean isFollowed = followRepository.existsByUserNoAndTargetUserNo(userNo, targetNo);
		
		ArrayList<FollowDTO> follows = preprocessingFollow(followRepository.getFollow(targetNo));
		ArrayList<FollowDTO> followers = preprocessingFollow(followRepository.getFollower(targetNo));
		Map<String,Object> map = new HashMap<>();
		map.put("postCount", (Object)postCount);
		map.put("isFollowed", (Object)isFollowed);
		map.put("Follow", (Object)follows);
		map.put("Follower", (Object)followers);
		return new CommonResponseDTO(map,HttpStatus.OK);
	}
	//유저 팔로우
	public CommonResponseDTO followUser(Long userNo,Long targetUserNo) {
		boolean isExistFollow = followRepository.existsByUserNoAndTargetUserNo(userNo, targetUserNo);
		Map map = new HashMap<>();
		if(isExistFollow) {
			Follow deleteFollow = followRepository.getFollow(userNo, targetUserNo);
			followRepository.delete(deleteFollow);
			ArrayList<FollowDTO> follows = preprocessingFollow(followRepository.getFollower(targetUserNo));
			map.put("Follow", (Object)follows);
			map.put("type", (String)"follow");
			return new CommonResponseDTO(map,HttpStatus.ACCEPTED);
		}
		Users user = userRepository.getById(userNo);
		Users targetUser = userRepository.getById(targetUserNo);
		Follow newFollow = new Follow(user,targetUser);
		followRepository.save(newFollow);
		ArrayList<FollowDTO> follows = preprocessingFollow(followRepository.getFollower(targetUserNo));
		map.put("Follow", (Object)follows);
		map.put("type", (String)"unfollow");
		return new CommonResponseDTO(map,HttpStatus.ACCEPTED);
	}
	//팔로우 팔로워 전처리
	private ArrayList<FollowDTO> preprocessingFollow(ArrayList<Follow> follows){
		ArrayList<FollowDTO> userList = new ArrayList<>();
		for(Follow f : follows) {
			userList.add(new FollowDTO(f.getUsers().getNickname(),f.getUsers().getNo()));
		}
		return userList;
	}
	//닉네임과 유저정포 페어 DTO 여기서만 사용될거같아서 private innerClass로 생성
	@Getter
	@Setter
	private class FollowDTO{
		private String nickname;
		private Long userNo;
		public FollowDTO(String nickname, Long userNo) {
			this.nickname = nickname;
			this.userNo = userNo;
		}
	}
}
