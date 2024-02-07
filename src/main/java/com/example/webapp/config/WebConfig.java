package com.example.webapp.config;

import com.example.webapp.Interceptors.BasicAuthInterceptor;
import com.example.webapp.Interceptors.PayloadCheckInterceptor;
import com.example.webapp.Interceptors.PostInterceptor;
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

    @Autowired
    PostInterceptor postInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(payloadCheckInterceptor).addPathPatterns("/healthz");

        registry.addInterceptor(basicAuthInterceptor).addPathPatterns("/v1/user/*");

        registry.addInterceptor(postInterceptor).addPathPatterns("/v1/user");
    }
}