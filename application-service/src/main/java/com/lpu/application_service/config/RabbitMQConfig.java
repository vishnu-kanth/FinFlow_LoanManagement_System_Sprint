package com.lpu.application_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange
    public static final String EXCHANGE = "finflow.topic.exchange";

    // Queues
    public static final String ADMIN_APPLICATION_EVENTS_QUEUE = "admin.application.events";
    public static final String DOCUMENT_APPLICATION_EVENTS_QUEUE = "document.application.events";
    public static final String DECISION_EVENTS_QUEUE = "application.decision.events";

    // Routing Keys
    public static final String KEY_APP_SUBMITTED  = "application.submitted";
    public static final String KEY_APP_CANCELLED  = "application.cancelled";
    public static final String KEY_DECISION_MADE  = "decision.made";

    @Bean
    public TopicExchange finflowExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue adminApplicationEventsQueue() {
        return QueueBuilder.durable(ADMIN_APPLICATION_EVENTS_QUEUE).build();
    }

    @Bean
    public Queue documentApplicationEventsQueue() {
        return QueueBuilder.durable(DOCUMENT_APPLICATION_EVENTS_QUEUE).build();
    }

    @Bean
    public Queue decisionEventsQueue() {
        return QueueBuilder.durable(DECISION_EVENTS_QUEUE).build();
    }

    @Bean
    public Binding adminApplicationEventsBinding(Queue adminApplicationEventsQueue, TopicExchange finflowExchange) {
        return BindingBuilder.bind(adminApplicationEventsQueue)
                .to(finflowExchange)
                .with("application.*");
    }

    @Bean
    public Binding documentApplicationEventsBinding(Queue documentApplicationEventsQueue, TopicExchange finflowExchange) {
        return BindingBuilder.bind(documentApplicationEventsQueue)
                .to(finflowExchange)
                .with("application.*");
    }

    @Bean
    public Binding decisionEventsBinding(Queue decisionEventsQueue, TopicExchange finflowExchange) {
        return BindingBuilder.bind(decisionEventsQueue)
                .to(finflowExchange)
                .with(KEY_DECISION_MADE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
