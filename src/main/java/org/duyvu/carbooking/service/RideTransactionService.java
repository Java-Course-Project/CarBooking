package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.RideTransaction;
import org.duyvu.carbooking.mapper.RideTransactionRequestToRideTransactionMapper;
import org.duyvu.carbooking.mapper.RideTransactionToRideTransactionResponseMapper;
import org.duyvu.carbooking.model.RideTransactionStatus;
import org.duyvu.carbooking.model.request.RideTransactionRequest;
import org.duyvu.carbooking.model.response.RideTransactionResponse;
import org.duyvu.carbooking.repository.RideTransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideTransactionService {
	private final RideTransactionRepository rideTransactionRepository;

	public Page<RideTransactionResponse> findAll(Pageable pageable) {
		return rideTransactionRepository.findAll(pageable).map(RideTransactionToRideTransactionResponseMapper.INSTANCE::map);
	}

	public RideTransactionResponse findById(Long id) {
		if (rideTransactionRepository.existsById(id)) {
			throw new EntityNotFoundException("RideTransaction not found");
		}

		return RideTransactionToRideTransactionResponseMapper.INSTANCE.map(
				rideTransactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transaction not found")));
	}

	@Transactional
	public Long save(RideTransactionRequest request) {
		RideTransaction rideTransaction = RideTransactionRequestToRideTransactionMapper.INSTANCE.map(request);
		rideTransaction.setRideTransactionStatus(RideTransactionStatus.ASSIGNED);
		return rideTransactionRepository.save(rideTransaction).getId();
	}
}