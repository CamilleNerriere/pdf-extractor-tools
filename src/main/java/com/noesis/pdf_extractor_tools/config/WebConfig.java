package com.noesis.pdf_extractor_tools.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.noesis.pdf_extractor_tools.middleware.ClassicRateLimiter;
import com.noesis.pdf_extractor_tools.middleware.JwtAuthInterceptor;
import com.noesis.pdf_extractor_tools.middleware.StrictRateLimiter;



@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ClassicRateLimiter classicRateLimiter;

    private final StrictRateLimiter strictRateLimiter;


    public WebConfig(ClassicRateLimiter classicRateLimiter, StrictRateLimiter strictRateLimiter,
            JwtAuthInterceptor jwtAuthInterceptor) {
        this.classicRateLimiter = classicRateLimiter;
        this.strictRateLimiter = strictRateLimiter;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        System.out.println("✔ WebConfig initialisé");
        registry.addInterceptor(classicRateLimiter).addPathPatterns("/**").excludePathPatterns("/extract/**",
                "/auth/**");
        registry.addInterceptor(strictRateLimiter).addPathPatterns("/auth/**");
    }

}
