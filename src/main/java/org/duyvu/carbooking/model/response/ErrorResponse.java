package org.duyvu.carbooking.model.response;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record ErrorResponse(String errorCode, List<String> parameters, Instant timestamp, String path) {

}
