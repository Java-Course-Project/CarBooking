package org.duyvu.carbooking.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.response.TransportationTypeResponse;
import org.duyvu.carbooking.service.TransportationTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transportation-types")
public class TransportationTypeController {
	private final TransportationTypeService transportationTypeRepository;

	@GetMapping("")
	public ResponseEntity<List<TransportationTypeResponse>> findAll() {
		return ResponseEntity.ok(transportationTypeRepository.findAll());
	}
}
