package com.lpu.application_service.messaging;

import com.lpu.application_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishApplicationSubmitted(Long applicationId, Long userId) {
        ApplicationEvent event = new ApplicationEvent(
                applicationId, userId, "SUBMITTED", "SUBMITTED", LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.KEY_APP_SUBMITTED,
                event
        );
        log.info("[RabbitMQ] Published application.submitted event for applicationId={}", applicationId);
    }

    public void publishApplicationCancelled(Long applicationId, Long userId) {
        ApplicationEvent event = new ApplicationEvent(
                applicationId, userId, "CANCELLED", "CANCELLED", LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.KEY_APP_CANCELLED,
                event
        );
        log.info("[RabbitMQ] Published application.cancelled event for applicationId={}", applicationId);
    }
}
