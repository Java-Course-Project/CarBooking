package org.duyvu.carbooking.utils.distributed;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributedObject {
	private final RedissonClient client;

	private static final String OBJECT_KEY_PREFIX = "object_";

	public <T> T get(String name) {
		RBucket<T> bucket = client.getBucket(OBJECT_KEY_PREFIX + name);
		return bucket.get();
	}

	public <T> void set(String name, T value, Duration timeout) {
		RBucket<T> bucket = client.getBucket(OBJECT_KEY_PREFIX + name);
		bucket.set(value, timeout);
	}

	public void delete(String name) {
		client.getBucket(OBJECT_KEY_PREFIX + name).delete();
	}
}
