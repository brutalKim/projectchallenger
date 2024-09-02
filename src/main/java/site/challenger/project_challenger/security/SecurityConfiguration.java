package site.challenger.project_challenger.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

	private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

	@Autowired
	public SecurityConfiguration(CustomOAuth2SuccessHandler customOAuth2SuccessHandler) {
		this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
		return web -> web.ignoring()
				// error endpoint를 열어줘야 함, favicon.ico 추가!
				.requestMatchers("/error", "/favicon.ico");
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(auth -> {

			auth.requestMatchers("/h2-console/**").permitAll();
			auth.anyRequest().authenticated();
		}); // 모든 요청에 인증 요구

		http.sessionManagement(session -> {
			session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			// 세션 만들지도않고 사용도 안함
		});
//		http.httpBasic(); // base64 기반 basic Authentication 사용안함
		http.csrf().disable(); // csrf 불가
		http.headers().frameOptions().sameOrigin(); // h2-console 허용하기위함 production 시에는 없애야할 수 있음

		// OAuth2 custom
		http.oauth2Login(oauth2Login -> oauth2Login
				.userInfoEndpoint(
						userInfo -> userInfo.userService(oAuth2UserService()).oidcUserService(oidcUserService()))
				.successHandler(customOAuth2SuccessHandler));

		// 1. jwt 설정 OAuth2 resource server set
		// 2. jwt decoder 정의해줘야함 맨밑
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

		return http.build();
	}
	// OAuth2 custom

	@Bean
	public CustomOAuth2UserService oAuth2UserService() {
		return new CustomOAuth2UserService();
	}

	@Bean
	public OidcUserService oidcUserService() {
		return new OidcUserService();
	}

	// h2 db
//	@Bean
//	public DataSource dataSource() {
//
//		// .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
//		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
//	}

//	@Bean
//	public UserDetailsService userDeatailService(DataSource dataSource) {
//
//		var user = User.withUsername("insu").password("insu").passwordEncoder(str -> passwordEncoder().encode(str))
//				.roles("USER").build();
//
//		var admin = User.withUsername("admin").password("admin").passwordEncoder(str -> passwordEncoder().encode(str))
//				.roles("ADMIN", "USER").build();
//
//		var jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//		jdbcUserDetailsManager.createUser(user);
//		jdbcUserDetailsManager.createUser(admin);
//		return jdbcUserDetailsManager;
//	}

//	@Bean
//	public BCryptPasswordEncoder passwordEncoder() {
//
//		// default strength value = 10 숫자를 올릴수록 복잡도가 올라감
//		return new BCryptPasswordEncoder();
//	}

}
