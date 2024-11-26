package site.challenger.project_challenger.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.constants.MyRole;
import site.challenger.project_challenger.domain.LocationRef;
import site.challenger.project_challenger.domain.OauthRef;
import site.challenger.project_challenger.domain.UserRoleRef;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersAuthority;
import site.challenger.project_challenger.domain.UsersAuthorityRef;
import site.challenger.project_challenger.domain.UsersRole;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.authentication.SignupServiceReqDTO;
import site.challenger.project_challenger.repository.LocationRefRepository;
import site.challenger.project_challenger.repository.OauthRefRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRefRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRepository;
import site.challenger.project_challenger.repository.UsersRoleRefRepository;
import site.challenger.project_challenger.repository.UsersRoleRepository;
import site.challenger.project_challenger.util.InsuUtils;
import site.challenger.project_challenger.util.JwtTokenManagement;

@Transactional
@Service
@RequiredArgsConstructor
public class UserManegementService {
	private final UserRepository userRepository;
	private final LocationRefRepository locationRefRepository;
	private final OauthRefRepository oauthRefRepository;
	private final UsersRoleRepository usersRoleRepository;
	private final UsersRoleRefRepository usersRoleRefRepository;
	private final UsersAuthorityRepository usersAuthorityRepository;
	private final UsersAuthorityRefRepository usersAuthorityRefRepository;
	private final JwtTokenManagement jwtTokenManagement;

	public CommonResponseDTO saveUser(SignupServiceReqDTO signupServiceReqDTO) {
		CommonResponseDTO res = null;
		try {
			if (doesNickNameExist(signupServiceReqDTO.getNickname())) {
				throw InsuUtils.throwNewResponseStatusException(HttpStatus.CONFLICT, "중복된 닉네임");
			}

			if (checkUser(signupServiceReqDTO.getId())) {
				throw InsuUtils.throwNewResponseStatusException(HttpStatus.CONFLICT, "이미 회원");
			} else {
				// locationRef를 참조
				LocationRef locationRef = locationRefRepository.findByOpt1AndOpt2(signupServiceReqDTO.getLocationOpt1(),
						signupServiceReqDTO.getLocationOpt2());
				// DTO에서 정보 추출
				String uid = signupServiceReqDTO.getId();
				String nickname = signupServiceReqDTO.getNickname();
				int oauthRefNo = signupServiceReqDTO.getOauthRef();
				// 유저정보 저장
				// 지역정보 조회
				Optional<OauthRef> OptionalOuathRef = oauthRefRepository.findById((long) oauthRefNo);
				// 지역정보 무결성확인
				if (OptionalOuathRef.isPresent()) {
					OauthRef oauthRef = OptionalOuathRef.get();
					Users user = new Users(uid, nickname, null, oauthRef, locationRef, true);
					Users savedUser = userRepository.save(user);
					Optional<UserRoleRef> optionalUserRoleRef = usersRoleRefRepository.findByRole("ROLE_USER");
					UserRoleRef userRoleRef = optionalUserRoleRef.get();
					usersRoleRepository.save(new UsersRole(user, userRoleRef));
					// WRITE READ REPORT 권한 insu 1124
					UsersAuthorityRef writeRef = getAuthorityRefByAuthority(MyRole.WRITE);
					UsersAuthorityRef readRef = getAuthorityRefByAuthority(MyRole.READ);
					UsersAuthorityRef reportRef = getAuthorityRefByAuthority(MyRole.REPORT);
					UsersAuthority writeAuth = new UsersAuthority();
					UsersAuthority readAuth = new UsersAuthority();
					UsersAuthority reportAuth = new UsersAuthority();

					writeAuth.setUser(savedUser);
					writeAuth.setUsersAuthorityRef(writeRef);
					readAuth.setUser(savedUser);
					readAuth.setUsersAuthorityRef(readRef);
					reportAuth.setUser(savedUser);
					reportAuth.setUsersAuthorityRef(reportRef);

					usersAuthorityRepository.save(writeAuth);
					usersAuthorityRepository.save(readAuth);
					usersAuthorityRepository.save(reportAuth);

					// transactional 이라 토큰 발행 시 userNo이 안들어가서 여기서 토큰 발급이 의미가 없음 && 유저 닉네임 중복검사가 필요함.

//					String token = jwtTokenManagement.issueJwtToken(uid);
//					if (token == null) {
//						// signupResDTO = new SignupResDTO(false,"비회원");
//						res = new CommonResponseDTO(HttpStatus.BAD_REQUEST, "비회원");
//					} else {
//						// signupResDTO = new SignupResDTO(true,token);
//						res = new CommonResponseDTO(HttpStatus.CREATED, "" + oauthRefNo);
//					}

					res = new CommonResponseDTO(HttpStatus.CREATED, "" + oauthRefNo);

					// 의미없음
				}
			}
		} catch (Exception e) {
			// 그 외의 모든 예외 처리
			e.printStackTrace();
			res = new CommonResponseDTO(HttpStatus.CONFLICT, e.getMessage());
		}
		return res;
	}

	private UsersAuthorityRef getAuthorityRefByAuthority(String authority) {
		return usersAuthorityRefRepository.findByAuthority(authority)
				.orElseThrow(() -> InsuUtils.throwNewResponseStatusException("존재하지 않는 권한"));
	}

	private boolean checkUser(String id) {
		return userRepository.existsByUid(id);
	}

	private boolean doesNickNameExist(String nickname) {
		return userRepository.existsByNickname(nickname);
	}

	public String login(Long userId) {
		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There's no such user"));
		return user.getNickname();
	}
}