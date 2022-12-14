package com.example.springsecurityproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class config implements WebMvcConfigurer {
    @Value("${upload.path}")
    private String uploadPath;

    //Тут делаем раздачу картинок

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        //** - какой либо тест
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:///" + uploadPath + "/");
    }


}
