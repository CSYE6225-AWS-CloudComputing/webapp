package com.example.webapp.Interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PayloadCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            if(request.getMethod().equalsIgnoreCase("HEAD") || request.getMethod().equalsIgnoreCase("OPTIONS")){
                response.setHeader("cache-control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma","no-cache");
                response.setHeader("X-Content-Type-Options","nosniff");
                response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
                return false;
            }
            if (request.getMethod().equalsIgnoreCase("GET")) {
                if (request.getContentLengthLong() > 0 || request.getParameterMap().size()>0) {
                    response.setHeader("cache-control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma","no-cache");
                    response.setHeader("X-Content-Type-Options","nosniff");
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return false;
                }
            }
        }

        return true;
    }
}
