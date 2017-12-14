package com.example.reactivestream;

import io.reactivex.Flowable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.reactivestreams.Subscriber;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Slf4j
@Order(100)
class FlowWithException {
    private final CamelContext camelContext;

    @PostConstruct
    public void produce() {
        CamelReactiveStreamsService camel = CamelReactiveStreams.get(camelContext);
        oddProduceException()
                .doOnSubscribe(x -> log.info("Start subscribe"))
                .doOnNext(x -> log.info("Value: {}", x))
                .doOnError(throwable -> log.info("Catch exception"))
                .doOnTerminate(() -> log.info("End subscribe"))
                .onErrorResumeNext(throwable -> {
                    return Flowable.just(throwable)
                            .doOnNext(x -> log.info("Send on exception-handler"))
                            .doOnNext(e -> Flowable.just(e)
                                    .subscribe(camel.subscriber("seda:exception-handler", Throwable.class)))
                            .flatMap(x -> Flowable.just(0L));
                })
                .subscribe();
    }

    private Flowable<Long> oddProduceException() {
        return Flowable.interval(10, TimeUnit.MILLISECONDS)
                .flatMap(number -> Flowable.just(number)
                        .filter(x -> x % 2 == 0)
                        .switchIfEmpty(Flowable.error(new RuntimeException("Problem")))
                );
    }
}
