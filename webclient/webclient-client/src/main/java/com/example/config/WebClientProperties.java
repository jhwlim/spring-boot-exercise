package com.example.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("web-client")
public class WebClientProperties {

    private final String baseUrl;

}
