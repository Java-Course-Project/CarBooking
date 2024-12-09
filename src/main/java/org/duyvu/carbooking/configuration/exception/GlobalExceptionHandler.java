package org.duyvu.carbooking.configuration.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.duyvu.carbooking.exception.TimeoutException;
import org.duyvu.carbooking.exception.UnsupportedValue;
import org.duyvu.carbooking.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
		log.error("", ex);
		// TODO: replace with error code
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
							 .body(ErrorResponse.builder()
												.errorCode(ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.parameters(List.of())
												.build());

	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
		log.error("", ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(ErrorResponse.builder()
												.errorCode(ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.parameters(List.of())
												.build());
	}

	@ExceptionHandler(UnsupportedValue.class)
	public ResponseEntity<ErrorResponse> handleUnsupportedValueException(UnsupportedValue ex, WebRequest request) {
		log.error("", ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(ErrorResponse.builder()
												.errorCode(ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.parameters(List.of())
												.build());
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
		log.error("", ex);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
							 .body(ErrorResponse.builder()
												.errorCode(ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.parameters(List.of())
												.build());
	}

	@ExceptionHandler(TimeoutException.class)
	public ResponseEntity<ErrorResponse> handleTimeoutException(TimeoutException ex, WebRequest request) {
		log.error("", ex);
		return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
							 .body(ErrorResponse.builder()
												.errorCode(ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.parameters(List.of())
												.build());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex, WebRequest request) {
		log.error("", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							 .body(ErrorResponse.builder()
												.errorCode(ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.parameters(List.of())
												.build());
	}
}
