package com.lpu.admin_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.lpu.admin_service.config.RabbitMQConfig.APPLICATION_EVENTS_QUEUE;

@Slf4j
@Component
public class ApplicationEventListener {

    @RabbitListener(queues = APPLICATION_EVENTS_QUEUE)
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("[RabbitMQ] Admin-Service received application event: applicationId={}, eventType={}, userId={}",
                event.getApplicationId(), event.getEventType(), event.getUserId());
    }
}
