package com.lpu.application_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.lpu.application_service.config.RabbitMQConfig.DECISION_EVENTS_QUEUE;

@Slf4j
@Component
public class DecisionEventListener {

    @RabbitListener(queues = DECISION_EVENTS_QUEUE)
    public void onDecisionReceived(DecisionEvent event) {
        log.info("[RabbitMQ] Received decision event: applicationId={}, decision={}, adminEmail={}",
                event.getApplicationId(), event.getDecision(), event.getAdminEmail());
    }
}
