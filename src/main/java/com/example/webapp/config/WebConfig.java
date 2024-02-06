package com.example.webapp.config;

import com.example.webapp.Interceptors.BasicAuthInterceptor;
import com.example.webapp.Interceptors.PayloadCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    BasicAuthInterceptor basicAuthInterceptor;

    @Autowired
    PayloadCheckInterceptor payloadCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(payloadCheckInterceptor).addPathPatterns("/healthz");

        registry.addInterceptor(basicAuthInterceptor).addPathPatterns("/v1/user/*");
    }
}