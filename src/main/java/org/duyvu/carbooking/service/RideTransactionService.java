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
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideTransactionService {
	private final RideTransactionRepository rideTransactionRepository;

	private final GeometryFactory geometryFactory;

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
		RideTransaction rideTransaction = RideTransactionRequestToRideTransactionMapper.INSTANCE.map(request, geometryFactory);
		rideTransaction.setRideTransactionStatus(RideTransactionStatus.ASSIGNED);
		return rideTransactionRepository.save(rideTransaction).getId();
	}

	@Transactional
	public Long updateStatus(Long id, RideTransactionStatus status) {
		RideTransaction rideTransaction = rideTransactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
				"Transaction not found"));

		rideTransaction.setRideTransactionStatus(status);
		return rideTransactionRepository.save(rideTransaction).getId();
	}

	public RideTransactionResponse findCurrentWaitingTransaction(Long driverId) {
		return RideTransactionToRideTransactionResponseMapper.INSTANCE.map(
				rideTransactionRepository.findCurrentWaitingTransaction(driverId, RideTransactionStatus.WAIT_FOR_CONFIRMATION)
										 .orElseThrow(() -> new EntityNotFoundException("No transaction waiting not found")));
	}
}