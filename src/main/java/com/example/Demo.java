package com.example;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lguz on 09.12.16.
 */
@Component
public class Demo {

    @Autowired
    private CamelContext camelContext;

    public Demo(CamelContext camelContext) {
        this.camelContext = camelContext;
        run();
    }

    public void run() {
        System.out.println(camelContext);
        camelContext = new DefaultCamelContext();
        Endpoint endpoint = camelContext.getEndpoint("rabbitmq://localhost:5672/tasks?username=admin&password=admin&autoDelete=false&routingKey=camel&queue=hello&autoAck=false");
        try {
            camelContext.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        send(endpoint);
        consume(endpoint);
    }

    public void send(Endpoint endpoint) {
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.setDefaultEndpoint(endpoint);
        producerTemplate.sendBody("one");
        producerTemplate.sendBody("two");
        producerTemplate.sendBody("done");
    }

    public void consume(Endpoint endpoint) {
        ConsumerTemplate consumerTemplate = camelContext.createConsumerTemplate();
        String body = null;
        while (!"done".equals(body)) {
            Exchange receive = consumerTemplate.receive(endpoint);
            body = receive.getIn()
                          .getBody(String.class);
            System.out.println(body);
        }

        try {
            camelContext.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
