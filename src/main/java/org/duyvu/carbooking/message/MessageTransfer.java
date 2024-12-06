package org.duyvu.carbooking.message;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import org.duyvu.carbooking.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageTransfer {
	private final Map<String, PriorityBlockingQueue<Message<?>>> queueMap = new ConcurrentHashMap<>();

	private static final int MAX_CAPACITY = 10000000;

	private PriorityBlockingQueue<Message<?>> getOrInit(String topic) {
		queueMap.putIfAbsent(topic, new PriorityBlockingQueue<>(MAX_CAPACITY, Comparator.comparingInt(Message::getPriority)));
		return queueMap.get(topic);
	}

	public void send(String topic, Message<?> message) {
		PriorityBlockingQueue<Message<?>> queue = getOrInit(topic);
		queue.add(message);
		// TODO: notify listener when new message are arrived
	}

	public Message<?> receive(String topic) {
		PriorityBlockingQueue<Message<?>> queue = getOrInit(topic);
		return queue.poll();
	}

	public Message<?> receive(String topic, Function<String, String> filter, long timeout) throws InterruptedException {
		PriorityBlockingQueue<Message<?>> queue = getOrInit(topic);
		synchronized (queue) {
			Instant startTime = Instant.now();
			while (true) {
				Message<?> message = queue.stream().filter(msg -> {
					for (Map.Entry<String, String> values :  msg.getHeaders().entrySet()) {
						if (filter.apply(values.getKey()).equals(values.getValue())) {
							return true;
						}
					}
					return false;
				}).findFirst().orElse(null);

				if (message != null) {
					queue.removeIf(msg -> {
						for (Map.Entry<String, String> values :  msg.getHeaders().entrySet()) {
							if (filter.apply(values.getKey()).equals(values.getValue())) {
								return true;
							}
						}
						return false;
					});
					return message;
				}
				if (Duration.between(startTime, Instant.now()).getSeconds() > timeout) {
					return null;
				}
				// TODO: sleep
				Thread.sleep(1000);
			}
		}
	}
}
