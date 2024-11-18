package site.challenger.project_challenger.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import site.challenger.project_challenger.domain.Follow;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.repository.FollowRepository;
import site.challenger.project_challenger.repository.PostRepository;
import site.challenger.project_challenger.repository.ProfileRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.util.InsuUtils;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final UserRepository userRepository;
	private final FollowRepository followRepository;
	private final ProfileRepository profileRepository;
	private final PostRepository postRepository;

	// target이 팔로우 하고 있는 수
	public CommonResponseDTO getFollowCount(long targetUserNo) {
		Map<String, Object> body = new HashMap<String, Object>();

		long followNum = getFollowNumByUserNo(targetUserNo);
		body.put("followNum", followNum);

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	// target을 팔로워 하고 있는 수
	public CommonResponseDTO getFollowerCount(long targetUserNo) {
		Map<String, Object> body = new HashMap<String, Object>();

		long followerNum = getFollowerNumByUserNo(targetUserNo);

		body.put("followerNum", followerNum);

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	// target이 팔로우

	// 요구사항
	// pageable
//	  nickname: string; // 
//    userNo: number; // 
//    followed: boolean; // 
//    userProfileImage?: string; // 
//    followNum: number; // 수정 
//    followerNum: number; // 수정
//    postCount: number;

	public CommonResponseDTO getFollowDetail(long requestUserNo, long targetUserNo, int page) {
		Map<String, Object> body = new HashMap<String, Object>();

		Users requestUser = getUserByUserNo(requestUserNo);
		Users targetUser = getUserByUserNo(targetUserNo);

		Pageable pageable = PageRequest.of(page, 10);

		Page<Follow> byUsers_No = followRepository.findByUsers_No(targetUserNo, pageable);
		InsuUtils.insertMapWithPageInfo(body, byUsers_No);

		List<Follow> content = byUsers_No.getContent();

		List<FollowDetailDto> detailList = new ArrayList<>();

		for (Follow follow : content) {
			FollowDetailDto dto = getDtoByRequestUserNoAndTargetUserNo(requestUserNo, follow.getFollowUsers().getNo());
			detailList.add(dto);
		}
		body.put("detailList", detailList);

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	// target을 팔로우
	public CommonResponseDTO getFollowerDetail(long requestUserNo, long targetUserNo, int page) {
		Map<String, Object> body = new HashMap<String, Object>();

		Users requestUser = getUserByUserNo(requestUserNo);
		Users targetUser = getUserByUserNo(targetUserNo);

		Pageable pageable = PageRequest.of(page, 10);

		Page<Follow> byUsers_No = followRepository.findByFollowUsers_No(targetUserNo, pageable);
		InsuUtils.insertMapWithPageInfo(body, byUsers_No);

		List<Follow> content = byUsers_No.getContent();

		List<FollowDetailDto> detailList = new ArrayList<>();

		for (Follow follow : content) {
			FollowDetailDto dto = getDtoByRequestUserNoAndTargetUserNo(requestUserNo, follow.getUsers().getNo());
			detailList.add(dto);
		}
		body.put("detailList", detailList);

		return new CommonResponseDTO(body, HttpStatus.OK, true);
	}

	private Users getUserByUserNo(long userNo) {
		return userRepository.findById(userNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 유저"));
	}

	private String getProfileImageByUserNo(long userNo) {
		return profileRepository.findByUser(getUserByUserNo(userNo))
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("프로필이 존재하지 않음")).getSavedName();
	}

	private boolean isAFollowB(long userA, long userB) {
		return followRepository.existsByUsers_NoAndFollowUsers_No(userA, userB);
	}

	private long getFollowNumByUserNo(long userNo) {
		return followRepository.countByUsers_No(userNo);
	}

	private long getFollowerNumByUserNo(long userNo) {
		return followRepository.countByFollowUsers_No(userNo);
	}

	private long getPostNumByUserNo(long userNo) {
		return postRepository.getPostCount(userNo);
	}

	private FollowDetailDto getDtoByRequestUserNoAndTargetUserNo(long requestUserNo, long targetUserNo) {
		FollowDetailDto dto = new FollowDetailDto();
		Users targetUsers = getUserByUserNo(targetUserNo);
		dto.setUserNo(targetUserNo);
		dto.setNickname(targetUsers.getNickname());
		dto.setUserProfileImage(getProfileImageByUserNo(targetUserNo));
		dto.setFollowed(isAFollowB(requestUserNo, targetUserNo));

		dto.setFollowNum(getFollowNumByUserNo(targetUserNo));
		dto.setFollowerNum(getFollowerNumByUserNo(targetUserNo));
		dto.setPostNum(getPostNumByUserNo(targetUserNo));

		return dto;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	class FollowDetailDto {
		private long userNo;
		private String nickname;
		private String userProfileImage;
		private boolean followed;
		private long followNum;
		private long followerNum;
		private long postNum;
	}

}
