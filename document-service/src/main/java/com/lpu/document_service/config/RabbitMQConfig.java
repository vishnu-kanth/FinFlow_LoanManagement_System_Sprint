package com.lpu.document_service.config;

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

    // Queue
    public static final String APPLICATION_EVENTS_QUEUE = "document.application.events";

    @Bean
    public TopicExchange finflowExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue applicationEventsQueue() {
        return QueueBuilder.durable(APPLICATION_EVENTS_QUEUE).build();
    }

    @Bean
    public Binding applicationEventsBinding(Queue applicationEventsQueue, TopicExchange finflowExchange) {
        return BindingBuilder.bind(applicationEventsQueue)
                .to(finflowExchange)
                .with("application.*");
    }

    @SuppressWarnings("deprecation")
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
