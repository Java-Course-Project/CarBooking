package org.duyvu.carbooking.mapper;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface CoordinateToPointMapper extends Mapper<Coordinate, Point> {
	CoordinateToPointMapper INSTANCE = Mappers.getMapper(CoordinateToPointMapper.class);

	@Named("mapToPoint")
	default Point map(Coordinate coordinate, @Context GeometryFactory factory) {
		return factory.createPoint(coordinate);
	}

	@Override
	default Point map(Coordinate coordinate) {
		throw new RuntimeException();
	}
}
