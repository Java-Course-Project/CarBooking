package org.duyvu.carbooking.configuration.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import org.duyvu.carbooking.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
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
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							 .body(ErrorResponse.builder()
												.errorCode(ex.getMessage())
												.path(request.getContextPath())
												.timestamp(Instant.now())
												.parameters(List.of())
												.build());
	}
}
