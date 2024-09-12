package site.challenger.project_challenger.util;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.security.JwtProvider;
@RequiredArgsConstructor
@Component
public class JwtTokenManagement {
	private final JwtProvider jwtProvider;
	private final JwtEncoder jwtEncoder;
	private final UserRepository userRepository;
	public String issueJwtToken(String uid) {
		JwtClaimsSet claims;
		String token = null;
		if (userRepository.existsByUid(uid)) {
			claims = jwtProvider.forUser(uid);
			token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
		}
		return token;
	}
}