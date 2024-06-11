package com.nextu.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfigurer implements WebMvcConfigurer {

    @Value("${react.host}")
    private String authorizeUrl;
    @Value("${react.port}")
    private String authorizedPort;
    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(false)
                .parameterName("mediaType")
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String urlToBeAuthorize = "http://"+authorizeUrl;
        if(authorizedPort!=null && !authorizedPort.isEmpty()){
            urlToBeAuthorize =urlToBeAuthorize +":"+authorizedPort;
        }
        registry.addMapping("/**").allowedOrigins(
                urlToBeAuthorize,
                "http://storageapp.com",
                "https://storageapp.com",
                "http://storageapp.com/api",
                "https://storageapp.com/api"
        );
    }
}
