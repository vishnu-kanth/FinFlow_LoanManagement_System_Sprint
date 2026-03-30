package com.lpu.admin_service.messaging;

import com.lpu.admin_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DecisionEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishDecisionMade(Long applicationId, String decision, String remarks, String adminEmail) {
        DecisionEvent event = new DecisionEvent(
                applicationId, decision, remarks, adminEmail, LocalDateTime.now()
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.KEY_DECISION_MADE,
                event
        );
        log.info("[RabbitMQ] Published decision.made event: applicationId={}, decision={}", applicationId, decision);
    }
}
