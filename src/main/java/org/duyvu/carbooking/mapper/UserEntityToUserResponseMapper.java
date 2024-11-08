package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.User;
import org.duyvu.carbooking.model.UserResponse;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper
public interface UserEntityToUserResponseMapper extends Mapper<User, UserResponse> {
	UserEntityToUserResponseMapper INSTANCE = Mappers.getMapper(UserEntityToUserResponseMapper.class);

	@Override
	UserResponse map(User userRequest);
}
