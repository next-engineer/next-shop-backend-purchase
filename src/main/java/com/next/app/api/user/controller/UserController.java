package com.next.app.api.user.controller;

import com.next.app.api.user.entity.User;
import com.next.app.api.user.service.UserService;
import com.next.app.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "사용자 관리 API")
@CrossOrigin(origins = "http://localhost")
public class UserController {

    public static record LoginRequest(@Email String email, @NotBlank String password) {}

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @Operation(summary = "모든 사용자 조회")
    public List<User> getAllUsers(@RequestParam(defaultValue = "false") boolean includeDeleted) {
        return includeDeleted ? userService.listUsersAny() : userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "사용자 조회")
    public ResponseEntity<User> getUserById(@PathVariable Long id,
                                            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        return (includeDeleted ? userService.getUserByIdAny(id)
                : userService.getUserById(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    @Operation(summary = "회원가입")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "사용자 수정")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "사용자 삭제")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "JWT 토큰 발급")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            User user = userService.login(req.email(), req.password());
            String token = jwtTokenProvider.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("user", user, "token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
