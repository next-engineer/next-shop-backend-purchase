package com.next.app.api.user.controller;

import com.next.app.api.user.entity.User;
import com.next.app.api.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 목록 조회")
    @GetMapping
    public List<User> list() {
        return userService.getAllUsers();
    }

    @Operation(summary = "회원 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return userService.getUserById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }
}
