package com.ledinhtuyenbkdn.handlefailurerabbitmq.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue workQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-max-length", 10);
        args.put("x-dead-letter-exchange", "dead.exchange");
        Queue queue = new Queue("work-queue", true, false, false, args);
        return queue;
    }

    @Bean
    public Queue deadQueue() {
        return new Queue("dead-queue", true);
    }

    @Bean
    public Exchange workExchange() {
        return new DirectExchange("amq.direct");
    }

    @Bean
    public Exchange deadExchange() {
        return new DirectExchange("dead.exchange");
    }

    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(workQueue()).to(workExchange()).with("work-routing-key").noargs();
    }

    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(deadQueue()).to(deadExchange()).with("work-routing-key").noargs();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("dinosaur.rmq.cloudamqp.com", 5672);
        connectionFactory.setUsername("lwuxkkkl");
        connectionFactory.setVirtualHost("lwuxkkkl");
        connectionFactory.setPassword("1go9E97C0C3NcoP8NW19Ye-hmaE7X9q5");
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        return connectionFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}
