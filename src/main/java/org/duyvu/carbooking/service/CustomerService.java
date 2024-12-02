package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.Customer;
import org.duyvu.carbooking.mapper.CustomerRequestToCustomerMapper;
import org.duyvu.carbooking.mapper.CustomerToCustomerResponseMapper;
import org.duyvu.carbooking.model.CustomerStatus;
import org.duyvu.carbooking.model.request.CustomerRequest;
import org.duyvu.carbooking.model.response.CustomerResponse;
import org.duyvu.carbooking.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
	private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

	public Page<CustomerResponse> findAll(Pageable pageable) {
		return customerRepository.findAll(pageable).map(CustomerToCustomerResponseMapper.INSTANCE::map);
	}

	public CustomerResponse findById(Long id) {
		if (customerRepository.existsById(id)) {
			throw new EntityNotFoundException("Customer not found");
		}

		return CustomerToCustomerResponseMapper.INSTANCE.map(customerRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Customer not found")));
	}

	public Long save(CustomerRequest request) {
		Customer customer = CustomerRequestToCustomerMapper.INSTANCE.map(request);
		customer.setCustomerStatus(CustomerStatus.NOT_BOOKED);
		return customerRepository.save(customer).getId();
	}

	public Long update(Long id, CustomerRequest request) {
		if (customerRepository.existsById(id)) {
			throw new EntityNotFoundException("Customer not found");
		}

        final Customer customer = customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
		customer.setEmail(request.getEmail());
		customer.setGender(request.getGender());
		customer.setDob(request.getDob());
		customer.setCitizenIdentificationNumber(request.getCitizenIdentificationNumber());
		customer.setUsername(request.getUsername());
		customer.setPassword(passwordEncoder.encode(request.getPassword()));

		customer.setCustomerStatus(CustomerStatus.NOT_BOOKED);
        return customerRepository.save(customer).getId();
	}
}