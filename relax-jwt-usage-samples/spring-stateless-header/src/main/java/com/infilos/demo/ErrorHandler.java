package com.infilos.demo;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class ErrorHandler {
    
    @ExceptionHandler({HttpAction.class})
    public String httpCodeException(HttpServletResponse response, HttpAction action) {
        if (action instanceof UnauthorizedAction) {
            response.setStatus(HttpConstants.UNAUTHORIZED);
            return "请登录";
        } else if (action instanceof ForbiddenAction) {
            response.setStatus(HttpConstants.FORBIDDEN);
            return "你没有权限";
        }
        return "未知异常";
    }
}
