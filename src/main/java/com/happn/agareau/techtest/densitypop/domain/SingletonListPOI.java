package com.happn.agareau.techtest.densitypop.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
@Scope("singleton")
@NoArgsConstructor
@Getter
public class SingletonListPOI {

    private List<PointOfInterest> pointOfInterests = emptyList();

    public synchronized void setPointOfInterests(List<PointOfInterest> pointOfInterests) {
        this.pointOfInterests = pointOfInterests;
    }


}
