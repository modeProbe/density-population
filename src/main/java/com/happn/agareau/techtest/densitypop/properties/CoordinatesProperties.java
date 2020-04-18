package com.happn.agareau.techtest.densitypop.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Data
@ConfigurationProperties(prefix = "coordinates")
@ConstructorBinding
public class CoordinatesProperties {

    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

}
