package com.ledinhtuyenbkdn.handlefailurerabbitmq.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkMessageConsumer implements ChannelAwareMessageListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @param message
     * @param channel
     * @throws Exception
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        System.out.println("WORK MESSAGE: " + message);

        ObjectMapper objectMapper = new ObjectMapper();
        MessageDTO messageDTO = null;

        try {
            messageDTO = objectMapper.readValue(new String(message.getBody()), MessageDTO.class);
        } catch (Exception e) {
            rejectAndSendToDeadExchange(message, channel);
            return;
        }

        if (!isValidMessage(messageDTO)) {
            rejectAndSendToDeadExchange(message, channel);
        } else if (!checkBusinessLogic(messageDTO)) {
            requeueWithRetryMetadata(message, channel);
        } else {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            System.out.println("Consume successfully: " + messageDTO);
        }
    }

    private boolean isValidMessage(MessageDTO messageDTO) {
        if (messageDTO == null) {
            return false;
        }
        if (messageDTO.getName() == null || "".equals(messageDTO.getName()) || messageDTO.getAge() == null) {
            return false;
        }
        return true;
    }

    private boolean checkBusinessLogic(MessageDTO messageDTO) {
        if (messageDTO.getAge() <= 0) {
            return false;
        }
        return true;
    }

    public void rejectAndSendToDeadExchange(Message message, Channel channel) throws Exception {
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
    }

    public void requeueWithRetryMetadata(Message message, Channel channel) throws Exception {
        //send ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        //create brand new message and requeue
        final Integer MAX_RETRY_TIME = 5;
        Integer currentRetryTime = (Integer) message.getMessageProperties().getHeaders().get("x-retry-metadata");
        MessageProperties messageProperties = new MessageProperties();
        if (currentRetryTime == null) {
            messageProperties.setHeader("x-retry-metadata", MAX_RETRY_TIME - 1);
        } else if (currentRetryTime == 0) {
            rabbitTemplate.send("dead.exchange", "work-routing-key", message);
            return;
        } else {
            messageProperties.setHeader("x-retry-metadata", currentRetryTime - 1);
        }

        Message requeueMessage = new Message(message.getBody(), messageProperties);

        rabbitTemplate.send("amq.direct", "work-routing-key", requeueMessage);
    }
}
