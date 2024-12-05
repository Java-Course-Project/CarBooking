package org.duyvu.carbooking.model;

import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Message<T> {
	private T data;
	private Instant timestamp;
	private Map<String, String> headers;
	private int priority;
}
