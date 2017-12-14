package com.example.reactivestream;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.camel.component.reactive.streams.util.UnwrapStreamProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RouteConfig {

    @Bean
    RoutesBuilder timerReactiveStream() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer:clock")
                        .setBody()
                        .header(Exchange.TIMER_COUNTER)
                        .to("reactive-streams:numbers");
            }
        };
    }

    @Bean
    RoutesBuilder reactiveStreamProducerConsumerRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("reactive-streams:producer")
                        .to("seda:reactive-stream-event-consumer");
            }
        };
    }

    @Bean
    RoutesBuilder reactiveStreamExceptionHandlerRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:exception-handler")
                        .to("log:INFO");
            }
        };
    }

    @Bean
    RoutesBuilder replyRoute(ReplayConsumerBean consumer) {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                onException(RuntimeException.class)
                        .maximumRedeliveries(-1)
                        .redeliveryDelay(2000);

                from("rabbitmq://localhost:5672/reactive-stream-request-replay?username=admin&password=admin" +
                     "&autoDelete=false" +
                     "&queue=reactive-stream-request-replay-queue&routingKey=reactive-stream-request-replay-queue" +
                     "&autoAck=false")
                        .setHeader(RabbitMQConstants.REQUEUE)
                        .constant(true)
                        .log("Replay-Request ${body}")
                        .setBody(body().convertTo(String.class))
                        .bean(consumer)
                        .process(new UnwrapStreamProcessor());
            }
        };
    }
}
