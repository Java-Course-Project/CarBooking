package org.duyvu.carbooking.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.mapper.TransportationTypeToTransportationTypeResponseMapper;
import org.duyvu.carbooking.model.response.TransportationTypeResponse;
import org.duyvu.carbooking.repository.TransportationTypeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransportationTypeService {
	private final TransportationTypeRepository transportationTypeRepository;

	public List<TransportationTypeResponse> findAll() {
		return transportationTypeRepository.findAll().stream().map(TransportationTypeToTransportationTypeResponseMapper.INSTANCE::map)
										   .toList();
	}
}
