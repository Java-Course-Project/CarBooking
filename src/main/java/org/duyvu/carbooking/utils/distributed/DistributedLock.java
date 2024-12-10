package org.duyvu.carbooking.utils.distributed;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributedLock {
	private final RedissonClient client;

	private static final String SEMAPHORE_KEY_PREFIX = "semaphore_";

	private static final String LOCK_KEY_PREFIX = "lock_";

	public void wait(String name, Duration timeout) throws InterruptedException {
		RSemaphore semaphore = client.getSemaphore(SEMAPHORE_KEY_PREFIX + name);
		semaphore.drainPermits();
		semaphore.tryAcquire(timeout);
	}

	public void await(String name) {
		RSemaphore semaphore = client.getSemaphore(SEMAPHORE_KEY_PREFIX + name);
		semaphore.release();
	}

	public void delete(String name) {
		client.getSemaphore(SEMAPHORE_KEY_PREFIX + name).delete();
	}

	public boolean tryLock(String name, Duration timeout) throws InterruptedException {
		RLock lock = client.getLock(LOCK_KEY_PREFIX + name);
		return lock.tryLock(timeout.getSeconds(), TimeUnit.SECONDS);
	}

	public void unlock(String name) {
		RLock lock = client.getLock(LOCK_KEY_PREFIX + name);
		lock.unlock();
	}
}
