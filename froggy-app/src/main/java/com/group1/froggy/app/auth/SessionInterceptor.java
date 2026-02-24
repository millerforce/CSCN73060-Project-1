package com.group1.froggy.app.auth;

import com.group1.froggy.app.controllers.AuthorizationController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException {
        if (handler instanceof HandlerMethod hm) {
            boolean required = hm.hasMethodAnnotation(RequireSession.class)
                || AnnotatedElementUtils.hasAnnotation(hm.getBeanType(), RequireSession.class);
            if (required) {
                String cookie = request.getHeader(AuthorizationController.COOKIE_HEADER);
                if (cookie == null || cookie.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/problem+json");
                    response.getWriter().write("{\"type\":\"about:blank\",\"title\":\"Unauthorized\",\"status\":401,\"detail\":\"Cookie header with session is required\"}");
                    return false;
                }
            }
        }
        return true;
    }
}
