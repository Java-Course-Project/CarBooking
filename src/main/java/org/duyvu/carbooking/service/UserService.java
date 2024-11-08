package org.duyvu.carbooking.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.entity.User;
import org.duyvu.carbooking.mapper.UserEntityToUserResponseMapper;
import org.duyvu.carbooking.mapper.UserRequestToUserEntityMapper;
import org.duyvu.carbooking.model.UserRequest;
import org.duyvu.carbooking.model.UserResponse;
import org.duyvu.carbooking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;

	public boolean isExistByUsername(String username) {
		return userRepository.existsByName(username);
	}

	@Override
	public User loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByName(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
	}

	@Transactional
	public UUID save(UserRequest userRequest) {
		return userRepository.save(UserRequestToUserEntityMapper.INSTANCE.map(userRequest)).getId();
	}

	public Page<UserResponse> getUsers(Pageable pageable) {
		return userRepository.findAll(pageable).map(UserEntityToUserResponseMapper.INSTANCE::map);
	}

	public UserResponse getUser(UUID id) {
		return userRepository.findById(id).map(UserEntityToUserResponseMapper.INSTANCE::map)
							 .orElseThrow(() -> new EntityNotFoundException("User not found"));
	}
}
