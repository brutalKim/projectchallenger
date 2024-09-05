package site.challenger.project_challenger.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class CorsConfig {
	@Bean
    public CorsConfigurationSource corsConfigurationSource(HttpServletRequest request) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://localhost:3000"); // 도메인 허용
        configuration.addAllowedMethod(""); // 모든 HTTP 메소드 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용
        configuration.addAllowedHeader(""); // 모든 헤더 허용
        configuration.setMaxAge(3600L); // cors허용 캐싱시간
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정 적용
        return source;
    }
}
