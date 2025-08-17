package com.next.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = tokenProvider.resolveToken(request);

        if (token == null) {
            // 필요시 주석 풀고 1~2분만 확인
            // log.debug("Authorization header missing or malformed for {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        if (!tokenProvider.validateToken(token)) {
            // log.debug("Invalid/expired token for {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = tokenProvider.getUserId(token);
        if (userId == null) {
            // log.debug("Token parsed but userId claim is null for {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String email = tokenProvider.getEmailFromToken(token);
        List<String> roles = tokenProvider.getRoles(token);
        var authorities = roles.stream().map(SimpleGrantedAuthority::new).toList();

        var principal = new CustomUserPrincipal(userId, email, authorities);
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
