package site.challenger.project_challenger.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

	public CustomJwtAuthenticationConverter() {
		super();
		setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
	}

	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		Map<String, Object> claims = jwt.getClaims();
		List<String> roles = (List<String>) claims.getOrDefault("roles", List.of());
		return roles.stream().map(SimpleGrantedAuthority::new) // 'ROLE_' 접두사가 필요 없는 경우
				.collect(Collectors.toList());
	}
}
