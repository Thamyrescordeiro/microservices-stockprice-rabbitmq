package com.microservice.pricestock.conections;

import constants.RabbitMqConstants;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;


@Component
public class RabbitMqConection {

    private  static final String NAME_EXCHANGE = "amq.direct";
    private AmqpAdmin amqpAdmin;

    public RabbitMqConection(AmqpAdmin amqpAdmin){
        this.amqpAdmin = amqpAdmin;
    }

    private org.springframework.amqp.core.Queue file(String namefile){
        return new Queue(namefile,true,false,false);
    }

    private DirectExchange directExchange(){
    return new DirectExchange(NAME_EXCHANGE);
    }
    private Binding relationship(Queue file, DirectExchange exchange){
       return new Binding(file.getName(), Binding.DestinationType.QUEUE,exchange.getName(),file.getName(),null);
    }

    @PostConstruct
    private void add(){
      Queue stockQueue = this.file(RabbitMqConstants.STOCK_QUEUE);
      Queue stockPrice = this.file(RabbitMqConstants.STOCK_PRICE);

      DirectExchange exchange = this.directExchange();

      Binding stockConnection = this.relationship(stockQueue, exchange);
      Binding priceConnection = this.relationship(stockPrice,exchange);

      this.amqpAdmin.declareQueue(stockQueue);
      this.amqpAdmin.declareQueue(stockPrice);

      this.amqpAdmin.declareBinding(stockConnection);
      this.amqpAdmin.declareBinding(priceConnection);
    }
}
