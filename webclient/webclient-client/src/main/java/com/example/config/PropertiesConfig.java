package com.example.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.beans.ConstructorProperties;

@Configuration
@EnableConfigurationProperties(value = {WebClientProperties.class})
public class PropertiesConfig {
}
