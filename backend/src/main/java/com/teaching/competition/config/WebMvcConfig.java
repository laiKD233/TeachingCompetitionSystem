package com.teaching.competition.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.Arrays;
import java.nio.file.Paths;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final OssConfig ossConfig;

    @Value("${app.cors.allowed-origin-patterns:*}")
    private String allowedOriginPatterns;

    public WebMvcConfig(OssConfig ossConfig) {
        this.ossConfig = ossConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String configuredLocalPath = ossConfig.getLocalPath();
        String uploadPath = new File(configuredLocalPath).isAbsolute()
                ? configuredLocalPath
                : Paths.get(configuredLocalPath).toAbsolutePath().normalize().toString();
        String location = "file:" + uploadPath + File.separator;
        log.info("配置静态资源映射: /uploads/** -> {}", location);
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] originPatterns = Arrays.stream(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .toArray(String[]::new);

        registry.addMapping("/**")
                .allowedOriginPatterns(originPatterns)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
