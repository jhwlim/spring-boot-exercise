package com.example.producer;

import com.example.config.RabbitProperties;
import com.example.model.SimpleMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class ExampleProducer {

    private final RabbitProperties rabbitProperties;
    private final RabbitTemplate rabbitTemplate;

    public ExampleProducer(RabbitProperties rabbitProperties, RabbitTemplate rabbitTemplate) {
        this.rabbitProperties = rabbitProperties;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendHello() {
        rabbitTemplate.convertAndSend(rabbitProperties.getExchange(), rabbitProperties.getRoutingKey(), new SimpleMessage("Hello"));
    }

}
