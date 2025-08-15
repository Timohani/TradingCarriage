package org.timowa.megabazar.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.timowa.megabazar.database.entity.User;
import org.timowa.megabazar.service.LoginContext;
import org.timowa.megabazar.service.UserService;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final LoginContext loginContext;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieValue = cookie.getValue();
                if ("loginUser".equals(cookie.getName()) && cookieValue != null) {
                    User userFromCookie = userService.getObjectByUsername(cookieValue);
                    loginContext.setLoginUser(userFromCookie);
                }
            }
        }

        if (loginContext.getLoginUser() == null) {
            response.sendError(401);
            return false;
        }
        return true;
    }
}