package com.bbs.backend.config;

import com.bbs.backend.interceptor.CommentLoginCheckInterceptor;
import com.bbs.backend.interceptor.PostLoginCheckInterceptor;
import com.bbs.backend.interceptor.UserLoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PostLoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/bbs/**");

        registry.addInterceptor(new UserLoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/user/logout", "/user/yourAccount");

        registry.addInterceptor(new CommentLoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/comment/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3030")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }
}
