package com.group4.lumos_api.common.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.group4.lumos_api.auth.config.FirebaseConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 매 요청의 {@code Authorization: Bearer <Firebase ID Token>} 헤더를 검증해
 * 인증된 사용자(uid)를 SecurityContext에 등록한다.
 *
 * <p>토큰이 없거나 검증에 실패하면 SecurityContext를 비워 두며, 보호된 엔드포인트는
 * SecurityConfig에서 401로 거부된다.</p>
 */
@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String idToken = header.substring(BEARER_PREFIX.length()).trim();
            try {
                FirebaseConfig.initialize();
                FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(idToken);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(token.getUid(), null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // 검증 실패 시 인증 정보를 남기지 않는다 → SecurityConfig가 401 처리.
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
