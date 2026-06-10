package com.group4.lumos_api.common.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 인증된 사용자의 Firebase uid(String)를 컨트롤러 파라미터로 주입한다.
 *
 * <pre>
 * public ResponseEntity&lt;...&gt; getMe(&#64;CurrentUser String userId) { ... }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal
public @interface CurrentUser {
}
