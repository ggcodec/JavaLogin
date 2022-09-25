package com.haotchen.server.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haotchen.server.pojo.RespBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 未登录和token失效返回的结果
 */
@Component
public class RestAuthorizationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        PrintWriter printWriter = httpServletResponse.getWriter();
        RespBean fail = RespBean.fail("未登录,请登录后访问!");
        fail.setCode(401);
        printWriter.println(new ObjectMapper().writeValueAsString(fail));
        printWriter.flush();
        printWriter.close();
    }
}
