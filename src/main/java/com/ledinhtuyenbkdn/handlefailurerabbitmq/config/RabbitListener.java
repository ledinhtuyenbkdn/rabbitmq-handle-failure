package com.ledinhtuyenbkdn.handlefailurerabbitmq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitListener implements RabbitListenerConfigurer {

    private ConnectionFactory connectionFactory;

    private WorkMessageConsumer workMessageConsumer;

    private DeadMessageConsumer deadMessageConsumer;

    public RabbitListener(ConnectionFactory connectionFactory, WorkMessageConsumer workMessageConsumer, DeadMessageConsumer deadMessageConsumer) {
        this.connectionFactory = connectionFactory;
        this.workMessageConsumer = workMessageConsumer;
        this.deadMessageConsumer = deadMessageConsumer;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        //register work endpoint
        SimpleRabbitListenerEndpoint workQueueEndpoint = new SimpleRabbitListenerEndpoint();
        workQueueEndpoint.setId("Endpoint-work-queue");
        workQueueEndpoint.setQueueNames("work-queue");
        workQueueEndpoint.setMessageListener(workMessageConsumer);
        workQueueEndpoint.setupListenerContainer(messageListenerContainer());
        rabbitListenerEndpointRegistrar.registerEndpoint(workQueueEndpoint);
        //register dead endpoint
        SimpleRabbitListenerEndpoint deadQueueEndpoint = new SimpleRabbitListenerEndpoint();
        deadQueueEndpoint.setId("Endpoint-dead-queue");
        deadQueueEndpoint.setQueueNames("dead-queue");
        deadQueueEndpoint.setMessageListener(deadMessageConsumer);
        deadQueueEndpoint.setupListenerContainer(messageListenerContainer());
        rabbitListenerEndpointRegistrar.registerEndpoint(deadQueueEndpoint);
    }

    @Bean
    public MessageListenerContainer messageListenerContainer() {
        DirectMessageListenerContainer messageListenerContainer = new DirectMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(connectionFactory);
        messageListenerContainer.setDefaultRequeueRejected(false);
        messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return messageListenerContainer;
    }
}
