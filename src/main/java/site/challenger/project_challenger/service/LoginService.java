package site.challenger.project_challenger.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersAuthority;
import site.challenger.project_challenger.domain.UsersAuthorityRef;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.login.AfterLoginInfoDTO;
import site.challenger.project_challenger.dto.login.UserAuthDto;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRefRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRepository;
import site.challenger.project_challenger.util.InsuUtils;

@Service
@RequiredArgsConstructor
public class LoginService {
	private final UserRepository userRepository;
	private final UsersAuthorityRepository usersAuthorityRepository;
	private final UsersAuthorityRefRepository usersAuthorityRefRepository;

	public CommonResponseDTO afterLogin(long requestUserNo) {
		// 가입일
		// 닉네임
		// 프로필 설명
		// 프로필 이미지
		// LocationRef
		Users requestUser = getUserByUserNo(requestUserNo);
		requestUser.setLatestLoginDate(LocalDateTime.now());
		requestUser = userRepository.save(requestUser);

		AfterLoginInfoDTO afterLoginInfoDTO = new AfterLoginInfoDTO();

		afterLoginInfoDTO.setUserNo(requestUserNo);
		afterLoginInfoDTO.setUserCreateTime(requestUser.getSignupDate());
		afterLoginInfoDTO.setUserLocationRef(requestUser.getLocationRef());
		afterLoginInfoDTO.setUserNickName(requestUser.getNickname());
		afterLoginInfoDTO.setUserProfileDescription(requestUser.getProfile().getDescription());
		afterLoginInfoDTO.setUserProfileImage(requestUser.getProfile().getSavedName());

		// start auth insu 1124
//		List<UserAuthDto> userAuth = new ArrayList<UserAuthDto>();
//
//		if (!isSomeoneHasAuth(requestUser, MyRole.WRITE)) {
//			UserAuthDto fillAuthDto = fillAuthDto(requestUser, MyRole.WRITE);
//			userAuth.add(fillAuthDto);
//		}
//
//		if (!isSomeoneHasAuth(requestUser, MyRole.READ)) {
//			UserAuthDto fillAuthDto = fillAuthDto(requestUser, MyRole.READ);
//			userAuth.add(fillAuthDto);
//		}
//		if (!isSomeoneHasAuth(requestUser, MyRole.REPORT)) {
//			UserAuthDto fillAuthDto = fillAuthDto(requestUser, MyRole.REPORT);
//			userAuth.add(fillAuthDto);
//		}
//
//		if (!userAuth.isEmpty()) {
//			afterLoginInfoDTO.setUserAuth(userAuth);
//		}
		// ends auth

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("userInfo", afterLoginInfoDTO);

		CommonResponseDTO response = new CommonResponseDTO(responseMap, HttpStatus.OK, null, null, true);

		return response;
	}

	private Users getUserByUserNo(long requestUserNo) {
		return userRepository.findById(requestUserNo)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 유저"));
	}

	private boolean isSomeoneHasAuth(Users user, String auth) {
		UsersAuthorityRef reportAuthRef = getAuthorityRefByAuthority(auth);
		UsersAuthority authorityByUserAndAuthorityRef = getAuthorityByUserAndAuthorityRef(user, reportAuthRef);
		if (authorityByUserAndAuthorityRef.getDate() == null) {
			// 권한 문제없음
			return true;
		} else {
			// 권한 문제있음
			boolean overBanned = InsuUtils.isOverBanned(authorityByUserAndAuthorityRef.getDate());
			if (overBanned) {
				authorityByUserAndAuthorityRef.setComment(null);
				authorityByUserAndAuthorityRef.setDate(null);
				usersAuthorityRepository.save(authorityByUserAndAuthorityRef);
				return true;
			}

			return false;
		}

	}

	private UsersAuthorityRef getAuthorityRefByAuthority(String authority) {
		return usersAuthorityRefRepository.findByAuthority(authority)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 권한"));
	}

	private UsersAuthority getAuthorityByUserAndAuthorityRef(Users user, UsersAuthorityRef usersAuthorityRef) {
		return usersAuthorityRepository.findByUserAndUsersAuthorityRef(user, usersAuthorityRef).orElseThrow(
				() -> InsuUtils.throwNewResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자가 권한을 부여받지 못했음"));
	}

	private UserAuthDto fillAuthDto(Users user, String auth) {
		UsersAuthorityRef authorityRefByAuthority = getAuthorityRefByAuthority(auth);
		UsersAuthority authorityByUserAndAuthorityRef = getAuthorityByUserAndAuthorityRef(user,
				authorityRefByAuthority);

		UserAuthDto dto = new UserAuthDto();
		dto.setComment(authorityByUserAndAuthorityRef.getComment());
		dto.setDate(authorityByUserAndAuthorityRef.getDate());
		dto.setKind(auth);

		return dto;

	}

}
