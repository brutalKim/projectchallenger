package site.challenger.project_challenger.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import site.challenger.project_challenger.constants.MyRole;
import site.challenger.project_challenger.constants.SecurityConstants;
import site.challenger.project_challenger.filter.JwtTokenValidatorFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final JwtTokenValidatorFilter jwtTokenValidatorFilter;

	private static final String[] SECURED_URL = { "/admin/**" };
	/*
	 * open url에 있던거 혹시몰라 남겨둠 "springdoc.api-docs.path=/api-docs",
	 * "/swagger-ui.html", "/swagger-ui/**",
	 *
	 */

	private static final String[] OPEN_URL = { "/oauth2/**", "/api/v1/postimg/**", "/api/v1/debug", "/api/v1/login/**",
			"/api/v1/authentication/signup", "/api/v1/afterSuccess", "/userProfileImg/**", "api/v1/test/**",
			"/admin/**" };

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
		return web -> web.ignoring()
				// error endpoint를 열어줘야 함, favicon.ico 추가!
				.requestMatchers("/error", "/favicon.ico");
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		return http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// cors 설정 현재 3000허용
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				// csrf 설정 사용안함
				.csrf(csrf -> csrf.disable())
				.exceptionHandling((e) -> e.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
				.addFilterBefore(jwtTokenValidatorFilter, OAuth2LoginAuthenticationFilter.class)
				.authorizeHttpRequests(auth -> {
					// 모든 허용
					auth.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll().requestMatchers("/WEB-INF/**")
							.denyAll().requestMatchers(OPEN_URL).permitAll().anyRequest()
							.hasAnyRole(MyRole.USER, MyRole.ADMIN);
				})
				//
				.headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()))
				// base 64 basic 로그인 허용안함
				.httpBasic(hb -> hb.disable())
				// 폼 로그인 허용 안함
				.formLogin(fl -> fl.disable())
				//
				.oauth2Login(oauth -> oauth.successHandler(customOAuth2SuccessHandler))

				.build();

	}

	// 09.09 cors config
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080", "http://localhost:3001")); // 허용할
																														// 출처
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // 허용할 HTTP 메서드
		config.addAllowedHeader("*"); // 허용할 헤더
		config.setAllowCredentials(true); // 모든 인증 허용
		config.setExposedHeaders(Arrays.asList(SecurityConstants.JWT_HEADER)); // 이 헤더로 보낼것임 jwt
		config.setMaxAge(3600L); // cors허용 캐싱시간
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config); // 모든 경로에 대해 CORS 설정 적용
		return source;
	}
}
