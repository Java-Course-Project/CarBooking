package org.duyvu.carbooking.utils.locking;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributedUtils {
	private final RedissonClient client;
	private static final String LOCK_KEY_PREFIX = "lock_";
	private static final String OBJECT_KEY_PREFIX = "lock_";

	public void lock(String name, long timeout) {
		RLock lock = client.getFairLock(LOCK_KEY_PREFIX + name);
		lock.lock(timeout, TimeUnit.SECONDS);
	}

	public void unlock(String name) {
		RLock lock = client.getFairLock(LOCK_KEY_PREFIX + name);
		lock.unlock();
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
