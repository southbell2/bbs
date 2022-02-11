package com.bbs.backend.config;

import com.bbs.backend.interceptor.PostLoginCheckInterceptor;
import com.bbs.backend.interceptor.UserLoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PostLoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/bbs/**");

        registry.addInterceptor(new UserLoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/user/logout", "/user/yourAccount");
    }
}
