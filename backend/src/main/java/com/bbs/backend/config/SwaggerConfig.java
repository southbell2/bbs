package com.bbs.backend.config;

import com.bbs.backend.dto.ExceptionDTO;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerConfig {

    private final TypeResolver typeResolver;

    public SwaggerConfig(final TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    private static final ApiInfo DEFAULT_API_INFO = new ApiInfo("BBS API", "HTTP API FOR FRONT END", "1.0", "urn:tos",
            null, null, null);

    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(
            Arrays.asList("application/json")
    );

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(DEFAULT_API_INFO)
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES)
                .additionalModels(typeResolver.resolve(ExceptionDTO.class));
    }
}
