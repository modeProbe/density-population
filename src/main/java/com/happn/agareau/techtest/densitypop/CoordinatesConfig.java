package com.happn.agareau.techtest.densitypop;

import com.happn.agareau.techtest.densitypop.properties.CoordinatesProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoordinatesConfig {
    @Bean
    @ConfigurationProperties(prefix = "coordinates")
    public CoordinatesProperties coordinatesProperties() {
        return new CoordinatesProperties();
    }
}
