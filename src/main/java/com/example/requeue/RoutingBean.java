package com.example.requeue;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingBean {


    @Bean
    RoutesBuilder testRoute(HelloBean helloBean, AckBean ackBean) {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                onException(Exception.class)
                        .maximumRedeliveries(2)
                        .setHeader("rabbitmq.REQUEUE").constant(false)
                        .log("Zmieniam header na false");


                from("rabbitmq://localhost:5672/demo?username=admin&password=admin&autoDelete=false&queue=requeue_test&routingKey=requeue_test&autoAck=false")
                        .log("Ustawiam header")
                        .setHeader("rabbitmq.REQUEUE").constant(true)
                        .log("Idź do ACK 1")
                        .bean(ackBean)
                        .log("Idę do helloBean: ${body}")
                        .bean(helloBean)
                        .log("Idź do ACK 2")
                        .bean(ackBean)
                ;
            }
        };
    }
}
