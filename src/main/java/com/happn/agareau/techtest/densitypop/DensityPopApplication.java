package com.happn.agareau.techtest.densitypop;

import com.happn.agareau.techtest.densitypop.properties.CoordinatesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CoordinatesProperties.class)
public class DensityPopApplication {

    public static void main(String[] args) {
        SpringApplication.run(DensityPopApplication.class, args);
    }
}
