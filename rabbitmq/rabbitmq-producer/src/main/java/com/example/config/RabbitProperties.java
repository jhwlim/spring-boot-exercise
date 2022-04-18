package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties("example.rabbitmq")
public class RabbitProperties {

    private final String exchange;
    private final String queue;
    private final String routingKey;

    public RabbitProperties(String exchange, String queue, String routingKey) {
        this.exchange = exchange;
        this.queue = queue;
        this.routingKey = routingKey;
    }

    public String getExchange() {
        return exchange;
    }

    public String getQueue() {
        return queue;
    }

    public String getRoutingKey() {
        return routingKey;
    }

}
