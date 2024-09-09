package site.challenger.project_challenger.security;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
	public JwtClaimsSet forGuest(String id) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				//
				.issuer("project Challenge")
				//
				.issuedAt(Instant.now())
				//
				.expiresAt(Instant.now().plusSeconds(60 * 15))
				//
				.subject(id)
				//
				.claim("authorities", "ROLE_GUEST").build();
		return claims;
	}

	public JwtClaimsSet forUser(String id) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				//
				.issuer("project Challenge")
				//
				.issuedAt(Instant.now())
				//
				.expiresAt(Instant.now().plusSeconds(60 * 15))
				//
				.subject(id)
				//
				.claim("authorities", "").build();
		return claims;
	}

}
