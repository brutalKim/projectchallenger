package site.challenger.project_challenger.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.login.AfterLoginInfoDTO;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.util.InsuUtils;

@Service
@RequiredArgsConstructor
public class LoginService {
	private final UserRepository userRepository;

	public CommonResponseDTO afterLogin(long requestUserNo) {
		// 가입일
		// 닉네임
		// 프로필 설명
		// 프로필 이미지
		// LocationRef
		Users requestUser = getUserByUserNo(requestUserNo);

		AfterLoginInfoDTO afterLoginInfoDTO = new AfterLoginInfoDTO();

		afterLoginInfoDTO.setUserCreateTime(requestUser.getSignupDate());
		afterLoginInfoDTO.setUserLocationRef(requestUser.getLocationRef());
		afterLoginInfoDTO.setUserNickName(requestUser.getNickname());
		afterLoginInfoDTO.setUserProfileDescription(requestUser.getProfile().getDescription());
		afterLoginInfoDTO.setUserProfileImage(requestUser.getProfile().getSavedName());

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("userInfo", afterLoginInfoDTO);

		CommonResponseDTO response = new CommonResponseDTO(responseMap, HttpStatus.OK, null, null, true);

		return response;
	}

	private Users getUserByUserNo(long requestUserNo) {
		return userRepository.findById(requestUserNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 유저"));
	}

}
