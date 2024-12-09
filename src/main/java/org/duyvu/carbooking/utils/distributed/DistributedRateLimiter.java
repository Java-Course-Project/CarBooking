package org.duyvu.carbooking.utils.distributed;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedRateLimiter {
	private final RedissonClient client;

	public boolean tryAcquire(Duration timeout) {
		RRateLimiter rateLimiter = client.getRateLimiter("rate-limiter");
		rateLimiter.trySetRate(RateType.OVERALL, 100, Duration.ofSeconds(1));
		log.debug("Current permits {}", rateLimiter.availablePermits());
		return rateLimiter.tryAcquire(timeout);
	}
}
