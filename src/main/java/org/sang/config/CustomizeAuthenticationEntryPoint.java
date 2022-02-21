package org.sang.config;

import com.alibaba.fastjson.JSON;
import org.sang.response.JsonResult;
import org.sang.response.ResultCode;
import org.sang.response.ResultTool;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: hexin
 * @Description: 认证失败处理类 返回未授权;用来解决匿名用户访问无权限资源时的异常
 * @Date Create in 2022/2/20 21:35
 */
@Component
public class CustomizeAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        JsonResult result = ResultTool.fail(ResultCode.USER_NOT_LOGIN);
        httpServletResponse.setContentType("text/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(result));
    }
}
