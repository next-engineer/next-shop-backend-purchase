package com.next.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.stream.Stream;

@Configuration
public class CorsConfig {

    /**
     * 콤마(,)로 여러 개 입력 가능.
     * 예) APP_CORS_ALLOWED_ORIGINS=https://d9gv73ip2rojg.cloudfront.net,http://localhost:3000
     */
    @Value("${app.cors.allowed-origins:}")
    private String allowedOriginsEnv;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            Stream.of(allowedOriginsEnv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(cfg::addAllowedOriginPattern);
        } else {
            // 기본값: 로컬 + 클라우드 기본 도메인 패턴
            List<String> defaults = List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://*.amplifyapp.com",
                "https://*.cloudfront.net"
            );
            defaults.forEach(cfg::addAllowedOriginPattern);
        }

        cfg.addAllowedHeader("*");
        cfg.addAllowedMethod("*");
        cfg.setAllowCredentials(true);
        cfg.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
