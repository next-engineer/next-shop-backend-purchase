package com.next.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Next Java Server API",
                version = "1.0.0",
                description = "Spring Boot REST API Documentation with JWT Auth"
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth") // 모든 API에 기본 JWT 인증 적용
        }
)
@SecurityScheme(
        name = "bearerAuth",                // SecurityRequirement에서 참조하는 이름
        type = SecuritySchemeType.HTTP,     // HTTP 방식
        scheme = "bearer",                  // bearer 토큰 사용
        bearerFormat = "JWT",               // 토큰 포맷
        in = SecuritySchemeIn.HEADER        // Authorization 헤더에 포함
)
public class OpenApiConfig {
    // 별도의 메서드나 Bean 등록 없이 어노테이션만으로 Swagger JWT Authorize 버튼 생성
}
