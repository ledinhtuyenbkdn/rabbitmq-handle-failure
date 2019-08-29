package com.ledinhtuyenbkdn.handlefailurerabbitmq.controller;

import com.ledinhtuyenbkdn.handlefailurerabbitmq.config.MessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping("sendMessage")
    public String sendMessage(@RequestBody MessageDTO messageDTO) {
        rabbitTemplate.convertAndSend("amq.direct", "work-routing-key", messageDTO);
        return "sent message...";
    }
}
