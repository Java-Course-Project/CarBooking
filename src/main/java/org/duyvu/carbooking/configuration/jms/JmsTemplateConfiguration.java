package org.duyvu.carbooking.configuration.jms;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@RequiredArgsConstructor
public class JmsTemplateConfiguration {
	private final JmsTemplate jmsTemplate;

	@PostConstruct
	public void init() {
		jmsTemplate.setReceiveTimeout(1000);
	}
}
