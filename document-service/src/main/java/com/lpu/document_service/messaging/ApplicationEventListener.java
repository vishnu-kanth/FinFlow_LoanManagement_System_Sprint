package com.lpu.document_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.lpu.document_service.config.RabbitMQConfig.APPLICATION_EVENTS_QUEUE;

@Slf4j
@Component
public class ApplicationEventListener {

    @RabbitListener(queues = APPLICATION_EVENTS_QUEUE)
    public void onApplicationEvent(ApplicationEvent event) {
        if ("SUBMITTED".equals(event.getEventType())) {
            log.info("[RabbitMQ] Document-Service: Application {} SUBMITTED by userId={}. Ready to accept documents.",
                    event.getApplicationId(), event.getUserId());
            // Future: pre-create a document slot or notify user to upload docs
        } else if ("CANCELLED".equals(event.getEventType())) {
            log.info("[RabbitMQ] Document-Service: Application {} CANCELLED. Archiving related documents.",
                    event.getApplicationId());
            // Future: archive/soft-delete documents for this application
        }
    }
}
