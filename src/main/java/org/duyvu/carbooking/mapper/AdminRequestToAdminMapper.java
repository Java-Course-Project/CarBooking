package org.duyvu.carbooking.mapper;

import org.duyvu.carbooking.entity.Admin;
import org.duyvu.carbooking.model.request.AdminRequest;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper()
public interface AdminRequestToAdminMapper extends Mapper<AdminRequest, Admin> {
	AdminRequestToAdminMapper INSTANCE = Mappers.getMapper(AdminRequestToAdminMapper.class);
}