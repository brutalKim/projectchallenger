package site.challenger.project_challenger.security;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

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
	//jwt token 생성시 secretkey
	private String createToken(Authentication authentication) {
		var claims = JwtClaimsSet.builder().issuer("self").issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(60 * 15)).subject(authentication.getName())
				.claim("scope", createScope(authentication)).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	private String createScope(Authentication authentication) {

		return authentication.getAuthorities().stream().map(str -> str.getAuthority()).collect(Collectors.joining(" "));
	}
}

record JwtResponse(String token) {
}