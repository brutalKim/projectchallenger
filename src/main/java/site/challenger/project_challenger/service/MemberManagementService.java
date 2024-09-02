package site.challenger.project_challenger.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.MemberVO;
import site.challenger.project_challenger.repository.MemberRepository;


@Service
@RequiredArgsConstructor
public class MemberManagementService {
	private final MemberRepository memberRepository;
	
	public Optional<MemberVO> searchMember(String id){
		return memberRepository.findById(id);
	}
	public void saveMember(String id, String nickname) {
		memberRepository.save(new MemberVO(id,nickname));
	}
}
