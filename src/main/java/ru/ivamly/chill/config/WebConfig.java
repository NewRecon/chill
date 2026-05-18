package ru.ivamly.chill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer.usePathSegment(1).setDefaultVersion("1");
    }

    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api/{version}", HandlerTypePredicate.forAnnotation(RestController.class));
    }
}
