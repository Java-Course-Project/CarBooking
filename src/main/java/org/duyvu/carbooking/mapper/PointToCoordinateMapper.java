package org.duyvu.carbooking.mapper;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface PointToCoordinateMapper extends Mapper<Point, Coordinate> {
	PointToCoordinateMapper INSTANCE = Mappers.getMapper(PointToCoordinateMapper.class);

	@Override
	default Coordinate map(Point point) {
		return point.getCoordinate();
	}
}