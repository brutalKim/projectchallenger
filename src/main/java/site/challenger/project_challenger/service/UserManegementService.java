package site.challenger.project_challenger.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.LocationRef;
import site.challenger.project_challenger.domain.OauthRef;
import site.challenger.project_challenger.domain.UserRoleRef;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersRole;
import site.challenger.project_challenger.dto.CommonResponseDTO;
import site.challenger.project_challenger.dto.authentication.SignupResDTO;
import site.challenger.project_challenger.dto.authentication.SignupServiceReqDTO;
import site.challenger.project_challenger.repository.LocationRefRepository;
import site.challenger.project_challenger.repository.OauthRefRepository;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRefRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRepository;
import site.challenger.project_challenger.repository.UsersRoleRefRepository;
import site.challenger.project_challenger.repository.UsersRoleRepository;
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
			if(checkUser(signupServiceReqDTO.getId())) {
				//signupResDTO = new SignupResDTO(false,"중복된 아이디");
				res = new CommonResponseDTO(HttpStatus.CONFLICT,"중복된 아이디");
			}else {
			//locationRef를 참조
			LocationRef locationRef = locationRefRepository.findByOpt1AndOpt2(signupServiceReqDTO.getLocationOpt1(), signupServiceReqDTO.getLocationOpt2());
			//DTO에서 정보 추출
			String uid = signupServiceReqDTO.getId();
			String nickname = signupServiceReqDTO.getNickname();
			int oauthRefNo = signupServiceReqDTO.getOauthRef();
			//유저정보 저장
			//지역정보 조회
			Optional<OauthRef> OptionalOuathRef = oauthRefRepository.findById((long) oauthRefNo);
			//지역정보 무결성확인
			if(OptionalOuathRef.isPresent()) {
				OauthRef oauthRef = OptionalOuathRef.get();
				Users user = new Users(uid,nickname,null, oauthRef, locationRef , true);
				userRepository.save(user);
				Optional<UserRoleRef> optionalUserRoleRef = usersRoleRefRepository.findByRole("ROLE_USER");
				UserRoleRef userRoleRef = optionalUserRoleRef.get();
				usersRoleRepository.save(new UsersRole(user,userRoleRef));
				String token = jwtTokenManagement.issueJwtToken(uid);
				if(token == null) {
					//signupResDTO = new SignupResDTO(false,"비회원");
					res = new CommonResponseDTO(HttpStatus.BAD_REQUEST,"비회원");
				}else {
					//signupResDTO = new SignupResDTO(true,token);
					res = new CommonResponseDTO(HttpStatus.CREATED,token); 
				}
			}
			}
		}catch (Exception e) {
            // 그 외의 모든 예외 처리
            e.printStackTrace();
            res = new CommonResponseDTO(HttpStatus.CONFLICT, e.toString());
        }finally {
        	return res;
        }
	}
	private boolean checkUser(String id) {
		return userRepository.existsByUid(id);
	}
	public String login (Long userId) {
		Users user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There's no such user"));
		return user.getNickname();
	}
}