package com.happn.agareau.techtest.densitypop;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@AllArgsConstructor
@ConfigurationPropertiesScan("com.happn.agareau.techtest.densitypop.properties")
public class DensityPopApplication {

    public static void main(String[] args) {
        SpringApplication.run(DensityPopApplication.class, args);
    }
}
