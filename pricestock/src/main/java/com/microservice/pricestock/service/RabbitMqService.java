package com.microservice.pricestock.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqService {

    @Autowired
private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(String fileName, Object message){
        try {
            String messageJson = this.objectMapper.writeValueAsString(message);
            this.rabbitTemplate.convertAndSend(fileName,messageJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        }
}

