package org.duyvu.carbooking.utils.distributed;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributedRateLimiter {
	private final RedissonClient client;

	public void acquirePermitForRateLimiter(Duration timeout) {
		RRateLimiter rateLimiter = client.getRateLimiter("rate-limiter");
		rateLimiter.trySetRate(RateType.OVERALL, 100, Duration.ofSeconds(1));
		rateLimiter.tryAcquire(timeout);
	}
}
