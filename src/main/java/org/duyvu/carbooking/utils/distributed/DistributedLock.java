package org.duyvu.carbooking.utils.distributed;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributedLock {
	private final RedissonClient client;

	private static final String LOCK_KEY_PREFIX = "semaphore_";

	public void wait(String name, Duration timeout) throws InterruptedException {
		RSemaphore semaphore = client.getSemaphore(LOCK_KEY_PREFIX + name);
		semaphore.drainPermits();
		semaphore.tryAcquire(timeout);
	}

	public void await(String name) {
		RSemaphore semaphore = client.getSemaphore(LOCK_KEY_PREFIX + name);
		semaphore.release();
	}

}
