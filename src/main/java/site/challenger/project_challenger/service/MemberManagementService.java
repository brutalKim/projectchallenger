package site.challenger.project_challenger.service;

import java.util.Optional;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.domain.MemberVO;
import site.challenger.project_challenger.repository.MemberRepository;


@Service
@RequiredArgsConstructor
public class MemberManagementService {
	private final MemberRepository memberRepository;
	private final JwtDecoder jwtDecoder;
	public Optional<MemberVO> searchMember(String id){
		return memberRepository.findById(id);
	}
	public void saveMember(String id, String nickname) {
		String jwtString = getJWTString(id);
		System.out.println(jwtString);
		if(jwtString != null) {
			Jwt decodedId = jwtDecoder.decode(jwtString);
			System.out.println(decodedId.getTokenValue()+"@@@@@@@@@@@@@@@@@@@@@@@");
			Optional<MemberVO> optionalMemberVO = memberRepository.findById(id);
			if(optionalMemberVO.isEmpty()) {
				memberRepository.save(new MemberVO(id,nickname));
			}
		}
	}
	private String getJWTString (String token) {
		if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // Remove the "Bearer " prefix (length 7)
        }
		return null;
	}
}