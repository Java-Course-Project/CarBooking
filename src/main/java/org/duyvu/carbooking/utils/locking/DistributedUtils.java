package org.duyvu.carbooking.utils.locking;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributedUtils {
	private final RedissonClient client;

	private static final String LOCK_KEY_PREFIX = "semaphore_";

	private static final String OBJECT_KEY_PREFIX = "object_";

	public void wait(String name, Duration timeout) throws InterruptedException {
		RSemaphore semaphore = client.getSemaphore(LOCK_KEY_PREFIX + name);
		semaphore.drainPermits();
		semaphore.tryAcquire(timeout);
	}

	public void await(String name) {
		RSemaphore semaphore = client.getSemaphore(LOCK_KEY_PREFIX + name);
		semaphore.release();
	}

	public <T> T get(String name) {
		RBucket<T> bucket = client.getBucket(OBJECT_KEY_PREFIX + name);
		return bucket.get();
	}

	public <T> void set(String name, T value) {
		RBucket<T> bucket = client.getBucket(OBJECT_KEY_PREFIX + name);
		bucket.set(value);
	}
}
