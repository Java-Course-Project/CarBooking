package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.User;
import org.duyvu.carbooking.model.UserRequest;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface UserRequestToUserEntityMapper extends Mapper<UserRequest, User> {
	UserRequestToUserEntityMapper INSTANCE = Mappers.getMapper(UserRequestToUserEntityMapper.class);

	@Mapping(target = "id", ignore = true)
	@Override
	User map(UserRequest userRequest);
}
