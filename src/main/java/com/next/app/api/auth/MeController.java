package com.next.app.api.auth;

import com.next.app.security.CustomUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class MeController {

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Long id = principal != null ? principal.getId() : null;
        return new MeResponse(id);
    }

    public record MeResponse(Long userId) {}
}
