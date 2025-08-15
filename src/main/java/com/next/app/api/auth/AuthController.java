package com.next.app.api.auth;

import com.next.app.api.user.entity.User;
import com.next.app.api.user.service.UserService;
import com.next.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.authenticate(request.getEmail(), request.getPassword());

            String token = jwtTokenProvider.generateToken(user.getEmail());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            headers.add("Access-Control-Expose-Headers", "Authorization");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(Map.of(
                            "accessToken", token,
                            "user", user
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "이메일 또는 비밀번호가 올바르지 않습니다."));
        }
    }
}
