package com.bbs.backend.config;

import com.bbs.backend.interceptor.CommentLoginCheckInterceptor;
import com.bbs.backend.interceptor.PostLoginCheckInterceptor;
import com.bbs.backend.interceptor.UserLoginCheckInterceptor;
import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.ConcurrentHashMap;

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
                .allowedOrigins("http://localhost:3030", "http://bbs-frontend-vue.s3-website.ap-northeast-2.amazonaws.com/", "http://bbs.southbell2.net")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookieName("JSESSIONID");
        cookieSerializer.setCookiePath("/");
//        cookieSerializer.setDomainName("southbell2.net");

        return cookieSerializer;
    }

    @Bean
    public MapSessionRepository sessionRepository() {
        return new MapSessionRepository(new ConcurrentHashMap<>());
    }
}
