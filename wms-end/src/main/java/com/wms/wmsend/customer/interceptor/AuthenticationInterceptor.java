package com.wms.wmsend.customer.interceptor;

import com.wms.wmsend.common.login.LoginUser;
import com.wms.wmsend.common.login.LoginUserHolder;
import com.wms.wmsend.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("access-token");
        Claims claims = JwtUtil.parseToken(token);
        Long userId = claims.get("userId",Long.class);
        String username = claims.get("username",String.class);
        LoginUserHolder.setLoginUser(new LoginUser(userId,username));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LoginUserHolder.clear();
    }
}
