package com.system.hakeem.Configuration;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeometryConfig {

    @Bean
    public GeometryFactory geometryFactory() {
        // PrecisionModel: default, SRID 4326 (WGS 84)
        return new GeometryFactory(new PrecisionModel(), 4326);
    }

    //SRID : spatial reference , it determines how the data has been written and must be read ,
    // like what is the measurement used meters or degree the 4326 is default and the one used by Google Maps api

}

