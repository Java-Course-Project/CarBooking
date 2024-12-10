package org.duyvu.carbooking.message;

import jakarta.jms.DeliveryMode;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Queue;
import jakarta.validation.Valid;
import java.time.Duration;
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

	public void send(Topic topic, @Valid Message<?> msg) {
		final long ttl = 30000L;
		jmsTemplate.execute(session -> {
			Queue queue = session.createQueue(topic.value);
			MessageProducer producer = session.createProducer(queue);
			ObjectMessage message = session.createObjectMessage(msg);

			// Set custom headers
			message.setStringProperty("x_customer_id", msg.getId().toString());
			producer.send(message, DeliveryMode.PERSISTENT, msg.getPriority(), ttl);
			return null;
		});
	}

	@SuppressWarnings("unchecked")
	public <T> Message<T> receive(Topic topic, String selector, Duration timeout) {
		return (Message<T>) jmsTemplate.execute(session -> {
			Queue queue = session.createQueue(topic.value);
			MessageConsumer consumer = session.createConsumer(queue, selector);

			return consumer.receive(timeout.getSeconds()).getBody(Message.class);
		});
	}
}
