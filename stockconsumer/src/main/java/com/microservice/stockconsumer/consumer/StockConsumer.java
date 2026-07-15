package com.microservice.stockconsumer.consumer;

import constants.RabbitMqConstants;
import dto.StockDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class StockConsumer {

    @RabbitListener( queues = RabbitMqConstants.STOCK_QUEUE )
    private void consumer(StockDto stockDto){
        System.out.println(stockDto.productcode);
        System.out.println(stockDto.quantity);
        System.out.println("-----------------------------------------------------------");
    }
}
