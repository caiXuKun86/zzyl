package com.zzyl.framework.interceptor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.framework.web.service.TokenService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MemberInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public MemberInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是Controller层的请求，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader("authorization");
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new BaseException("认证失败");
        }
        Claims claims = null;
        // 解析token
        try {
            claims = tokenService.parseToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new BaseException("认证失败");
        }

        if (ObjectUtil.isEmpty(claims)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new BaseException("认证失败");
        }
        Long userId = MapUtil.get(claims, "userId", Long.class);
        if (ObjectUtil.isEmpty(userId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new BaseException("认证失败");
        }

        // 将用户id放入ThreadLocal中
        UserThreadLocal.set(userId);

        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}
