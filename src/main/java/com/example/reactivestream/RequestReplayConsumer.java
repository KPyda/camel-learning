package com.example.reactivestream;

import io.reactivex.Flowable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@AllArgsConstructor
@Slf4j
class RequestReplayConsumer {
    private final CamelContext camelContext;

    @PostConstruct
    public void consume() {
        CamelReactiveStreamsService camel = CamelReactiveStreams.get(camelContext);
        workRequeue(camel);
    }

    private void workRequeue(CamelReactiveStreamsService camel) {
        Flowable.fromPublisher(camel.from("rabbitmq://localhost:5672/reactive-stream-request-replay?username=admin&password=admin" +
                                          "&autoDelete=false" +
                                          "&queue=reactive-stream-request-replay-queue-from" +
                                          "&routingKey=reactive-stream-request-replay-queue-from" +
                                          "&autoAck=false"))
                .doOnNext(exchange -> exchange.getIn()
                                              .setHeader(RabbitMQConstants.REQUEUE, true))
                .doOnNext(x -> log.info("Got: {}", x))
                .flatMap(exchange -> Flowable.just(exchange)
                                             .map(Exchange::getIn)
                                             .map(ex -> ex.getBody(Long.class))
                                             .map(Long::valueOf)
                                             .filter(x -> x % 2 == 0)
                                             .doOnNext(aLong -> log.info("Even: {}", aLong))
                                             .map(x -> exchange)
                                             .switchIfEmpty(Flowable.just(exchange)
                                                                    .doOnNext(e -> e.setException(new RuntimeException())))
                        )
                .doOnTerminate(() -> log.info("Terminate"))
                .doOnComplete(() -> log.info("Complete"))
                .subscribe();
    }
}
