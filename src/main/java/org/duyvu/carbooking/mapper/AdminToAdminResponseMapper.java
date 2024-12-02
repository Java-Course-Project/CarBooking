package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.Admin;
import org.duyvu.carbooking.model.response.AdminResponse;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface AdminToAdminResponseMapper extends Mapper<Admin, AdminResponse> {
	AdminToAdminResponseMapper INSTANCE = Mappers.getMapper(AdminToAdminResponseMapper.class);
}
