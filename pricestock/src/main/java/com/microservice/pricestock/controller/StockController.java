package com.microservice.pricestock.controller;

import constants.RabbitMqConstants;
import dto.StockDto;
import com.microservice.pricestock.service.RabbitMqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("stock")
public class StockController {

    @Autowired
    private RabbitMqService rabbitMqService;

    @PutMapping
private ResponseEntity updateStock(@RequestBody StockDto stockDto){
        System.out.println(stockDto.productcode);
        this.rabbitMqService.sendMessage(RabbitMqConstants.STOCK_QUEUE, stockDto);
  return new ResponseEntity(HttpStatus.OK);
    }
}
