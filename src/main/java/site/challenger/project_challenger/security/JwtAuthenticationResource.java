package site.challenger.project_challenger.security;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import site.challenger.project_challenger.util.MyRole;

@Component
public class JwtAuthenticationResource {

	private JwtEncoder jwtEncoder;

	public JwtAuthenticationResource(JwtEncoder jwtEncoder) {
		super();
		this.jwtEncoder = jwtEncoder;
	}

	public String authenticate(Authentication authentication) {
		return new JwtResponse(createToken(authentication)).toString();
	}

	// jwt token 생성시 secretkey
	private String createToken(Authentication authentication) {
//		List<String> list = new ArrayList<>();
//		list.add(MyRole.GUEST);
		String[] list = { MyRole.GUEST };
		var claims = JwtClaimsSet.builder()
				// 토근 발급자
				.issuer("self")
				// 발급 시간
				.issuedAt(Instant.now())
				// 만료 시간
				.expiresAt(Instant.now().plusSeconds(60 * 15))
				// 사용자에 대한 식별자
				.subject(authentication.getName())
				// Oauth2 권한? 인거같으므로 일단 주석처리 지우지말것
//				.claim("scope", createScope(authentication))
				.claim("scope", "read write")
				// 역할 추가
//				.claim("roles", list)
				.build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	private String createScope(Authentication authentication) {

		return authentication.getAuthorities().stream().map(str -> str.getAuthority()).collect(Collectors.joining(" "));
	}
}

record JwtResponse(String token) {
}