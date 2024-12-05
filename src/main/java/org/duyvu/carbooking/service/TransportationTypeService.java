package org.duyvu.carbooking.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.Fare;
import org.duyvu.carbooking.entity.TransportationType;
import org.duyvu.carbooking.repository.TransportationTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransportationTypeService {
	private final TransportationTypeRepository transportationTypeRepository;

	@PostConstruct
	@Transactional
	public void initDefaultTransportationTypes() {
		if (!transportationTypeRepository.existsById(1)) {
			TransportationType transportationType = TransportationType.builder()
																	  .type("CAR")
																	  .id(1)
																	  .fare(Fare.builder()
																				.holidayRate(1.5)
																				.price(20000.0)
																				.normalHourRate(1.0)
																				.rushHourRate(1.2)
																				.normalDayRate(1.0)
																				.build())
																	  .build();
			transportationType.getFare().setTransportationType(transportationType);
			transportationTypeRepository.save(transportationType);
		}
		if (!transportationTypeRepository.existsById(2)) {
			TransportationType transportationType = TransportationType.builder()
																	  .type("MOTORBIKE")
																	  .id(2)
																	  .fare(Fare.builder()
																				.holidayRate(1.5)
																				.price(5000.0)
																				.normalHourRate(1.0)
																				.rushHourRate(1.2)
																				.normalDayRate(1.0)
																				.build())
																	  .build();
			transportationType.getFare().setTransportationType(transportationType);
			transportationTypeRepository.save(transportationType);
		}
	}
}
