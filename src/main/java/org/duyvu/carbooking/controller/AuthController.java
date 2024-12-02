package org.duyvu.carbooking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.duyvu.carbooking.model.request.LoginRequest;
import org.duyvu.carbooking.model.response.Token;
import org.duyvu.carbooking.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<Token> login(@RequestBody @Valid LoginRequest loginRequest) {
		return ResponseEntity.ok(authService.login(loginRequest));
	}

	@PostMapping("/refresh")
	public ResponseEntity<Token> refresh(@RequestBody @Valid @NotNull String refreshToken) {
		return ResponseEntity.ok(authService.refresh(refreshToken));
	}
}
