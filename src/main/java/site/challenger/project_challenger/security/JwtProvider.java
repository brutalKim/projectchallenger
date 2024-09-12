package site.challenger.project_challenger.security;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.constants.MyRole;
import site.challenger.project_challenger.domain.Users;
import site.challenger.project_challenger.domain.UsersAuthority;
import site.challenger.project_challenger.domain.UsersRole;
import site.challenger.project_challenger.repository.UserRepository;
import site.challenger.project_challenger.repository.UsersAuthorityRepository;
import site.challenger.project_challenger.repository.UsersRoleRepository;

@Component
@RequiredArgsConstructor
public class JwtProvider {

	private final UserRepository userRepository;
	private final UsersAuthorityRepository usersAuthorityRepository;
	private final UsersRoleRepository usersRoleRepository;

	public JwtClaimsSet forGuest(String uid, int oauthRef) {
		JwtClaimsSet claims = JwtClaimsSet.builder()
				//
				.issuer("project Challenge")
				//
				.issuedAt(Instant.now())
				//
				.expiresAt(Instant.now().plusSeconds(60 * 15))
				//
				.subject(uid)
				//
				.claim("oauthRef", oauthRef)
				//
				.claim("authorities", MyRole.ROLE_GUEST).build();
		return claims;
	}

	public JwtClaimsSet forUser(String uid) {

		Users user = userRepository.findByUid(uid).get();
		List<UsersAuthority> userAuthorities = usersAuthorityRepository.findByUser(user);
		List<UsersRole> userRoles = usersRoleRepository.findByUser(user);

		String authorities = populateAuthorities(userAuthorities, userRoles);

		JwtClaimsSet claims = JwtClaimsSet.builder()
				//
				.issuer("project Challenge")
				//
				.issuedAt(Instant.now())
				//
				.expiresAt(Instant.now().plusSeconds(60 * 15))
				//
				.subject(user.getNo().toString())
				//
				.claim("authorities", authorities).build();

		return claims;
	}

	private String populateAuthorities(List<UsersAuthority> userAuthorities, List<UsersRole> userRoles) {
		Set<String> authoritiesSet = new HashSet<>();
		for (UsersAuthority usersAuthority : userAuthorities) {
			String auth = usersAuthority.getUsersAuthorityRef().getUserRoleRef();
			authoritiesSet.add(auth);
		}
		for (UsersRole usersRole : userRoles) {
			String role = usersRole.getUserRoleRef().getRole();
			authoritiesSet.add(role);
		}
		return String.join(",", authoritiesSet);
	}
}
