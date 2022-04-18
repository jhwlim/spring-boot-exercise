package com.example.consumer;

import com.example.model.SimpleMessage;
import com.example.service.MessageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ExampleListener {

    private final MessageService messageService;

    public ExampleListener(MessageService messageService) {
        this.messageService = messageService;
    }

    @RabbitListener(queues = "${example.rabbitmq.queue}")
    public void receiveMessage(SimpleMessage message) {
        messageService.printOutSimpleMessage(message);
    }

}
