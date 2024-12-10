package org.duyvu.carbooking.message;

import jakarta.jms.DeliveryMode;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.validation.Valid;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duyvu.carbooking.model.Message;
import org.springframework.jms.core.JmsTemplate;
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

	private static final int MAX_PRIORITY = 9;

	public void send(Topic topic, @Valid Message<?> msg) {
		final long ttl = 30000L;
		jmsTemplate.execute(session -> {
			Queue queue = session.createQueue(topic.value);
			MessageProducer producer = session.createProducer(queue);
			ObjectMessage message = session.createObjectMessage(msg);

			// Set custom headers
			message.setStringProperty("x_custom_id", msg.getId().toString());
			producer.send(message, DeliveryMode.PERSISTENT, Math.min(msg.getPriority(), MAX_PRIORITY), ttl);
			return null;
		}, true);
	}

	@SuppressWarnings("unchecked")
	public <T> Message<T> receive(Topic topic, String selector, Duration timeout) {
		return (Message<T>) jmsTemplate.execute(session -> {
			Queue queue = session.createQueue(topic.value);
			MessageConsumer consumer = session.createConsumer(queue, selector);
			return Optional.ofNullable(consumer.receive(timeout.getSeconds() * 1000))
						   .map(e -> {
							   try {
								   return e.getBody(Message.class);
							   } catch (JMSException ex) {
								   throw new RuntimeException(ex);
							   }
						   }).orElse(null);
		}, true);
	}
}
