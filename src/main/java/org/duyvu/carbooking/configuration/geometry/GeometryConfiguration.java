package org.duyvu.carbooking.configuration.geometry;

import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeometryConfiguration {
	@Bean
	public GeometryFactory geometryFactory() {
		return new GeometryFactory();
	}
}
