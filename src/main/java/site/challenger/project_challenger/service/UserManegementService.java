package site.challenger.project_challenger.service;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.LocationRef;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.dto.authentication.SignupDTO;
import site.challenger.project_challenger.dto.authentication.SignupDTO.SignupResDTO;
import site.challenger.project_challenger.dto.authentication.SignupDTO.SignupServiceReqDTO;
import site.challenger.project_challenger.repository.LocationRefRepository;
import site.challenger.project_challenger.repository.UserRepository;

@RequiredArgsConstructor
public class UserManegementService {
	private final UserRepository userRepository;
	private final LocationRefRepository locationRefRepository;
	private final SignupDTO signupDTO;
	public SignupResDTO saveUser(SignupServiceReqDTO signupServiceReqDTO) {
		try {
			if(!checkUser(signupServiceReqDTO.getId())) {
				return signupDTO.new SignupResDTO(false,"중복된 아이디");
			}
			//locationRef를 참조
			LocationRef locationRef = locationRefRepository.findAllByop1Andop2(signupServiceReqDTO.getLocationOpt1(), signupServiceReqDTO.getLocationOpt2());
			//DTO에서 정보 추출
			String uid = signupServiceReqDTO.getId();
			String nickname = signupServiceReqDTO.getNickname();
			//유저정보 저장
			Users user = new Users(uid,nickname,null, null, locationRef,true);
			userRepository.save(user);
			return signupDTO.new SignupResDTO(true,"회원가입 성공");
		}catch (Exception e) {
            // 그 외의 모든 예외 처리
            e.printStackTrace();
            return signupDTO.new SignupResDTO(false,"서버 오류");
        }
	}
	private boolean checkUser(String id) {
		return userRepository.existsByUid(id);
	}
}