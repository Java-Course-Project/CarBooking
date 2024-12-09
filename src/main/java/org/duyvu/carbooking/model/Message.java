package org.duyvu.carbooking.model;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message<T> implements Serializable {
	private T data;

	private Instant timestamp;

	@NotNull
	private UUID id;

	private int priority;
}
