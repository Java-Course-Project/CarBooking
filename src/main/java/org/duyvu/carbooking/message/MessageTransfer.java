package org.duyvu.carbooking.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duyvu.carbooking.model.Message;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageTransfer {

	public enum Topic {
		CUSTOMER_BOOKING("CUSTOMER_BOOKING");

		private final String value;

		Topic(String value) {
			this.value = value;
		}
	}

	private final JmsTemplate jmsTemplate;

	private final ObjectMapper objectMapper;

	public void send(Topic topic, @Valid Message<?> msg) {
		final long ttl = 30000L;
		jmsTemplate.convertAndSend(topic.value, msg, new MessagePostProcessor() {
			@Override
			@NonNull
			public jakarta.jms.Message postProcessMessage(@NonNull jakarta.jms.Message message) throws JMSException {
				message.setJMSPriority(msg.getPriority());
				message.setJMSExpiration(System.currentTimeMillis() + ttl);
				message.setStringProperty("x_custom_id",msg.getId().toString());
				return message;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> Message<T> receive(Topic topic, String selector, Duration timeout) {
		Instant start = Instant.now();
		while (Duration.between(start, Instant.now()).compareTo(timeout) < 0) {
			Message<T> message = (Message<T>) jmsTemplate.receiveSelectedAndConvert(topic.value, selector);
			if (message != null) {
				return message;
			}
		}
		return null;
	}
}
