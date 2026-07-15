package com.microservice.pricestock.controller;

import constants.RabbitMqConstants;
import dto.PriceDto;
import com.microservice.pricestock.service.RabbitMqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("price")
public class PriceController {

    @Autowired
    private RabbitMqService rabbitMqService;

@PutMapping
    private ResponseEntity updatePrice(@RequestBody PriceDto priceDto){
    this.rabbitMqService.sendMessage(RabbitMqConstants.STOCK_PRICE,priceDto);
return new ResponseEntity(HttpStatus.OK);
    }


}
