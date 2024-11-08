package org.duyvu.carbooking.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.UserResponse;
import org.duyvu.carbooking.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Page<UserResponse>> getUsers(Pageable pageable) {
		return ResponseEntity.ok(userService.getUsers(pageable));
	}

	@GetMapping("/{id}")
	@PostAuthorize("hasRole('ROLE_ADMIN') OR returnObject.body.username == authentication.name")
	public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
		return ResponseEntity.ok(userService.getUser(id));
	}
}
