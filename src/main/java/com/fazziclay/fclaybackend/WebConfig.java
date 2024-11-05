package com.fazziclay.fclaybackend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Разрешить CORS для всех путей
                .allowedOriginPatterns("*") // Замените на ваш домен для большей безопасности
                .allowedMethods("*") // Укажите необходимые методы
                .allowedHeaders("*"); // Укажите разрешенные заголовки
    }
}