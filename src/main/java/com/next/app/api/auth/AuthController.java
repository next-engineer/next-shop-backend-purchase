package com.next.app.api.auth;

import com.next.app.api.user.entity.User;
import com.next.app.api.user.service.UserService;
import com.next.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 1) 이메일/비밀번호 검증 (BCrypt matches)
            User user = userService.login(request.getEmail(), request.getPassword());

            // 2) JWT 토큰 발급
            String token = jwtTokenProvider.generateToken(user.getEmail());

            // 3) 토큰 + 사용자 정보 반환 (프론트 연동용 accessToken 키 고정)
            return ResponseEntity.ok(Map.of(
                    "accessToken", token,
                    "user", user
            ));
        } catch (RuntimeException e) {
            // 4) 실패 시 401
            return ResponseEntity.status(401)
                    .body(Map.of("error", "이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }
}
