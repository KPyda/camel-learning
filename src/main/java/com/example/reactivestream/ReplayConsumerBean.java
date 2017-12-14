package com.example.reactivestream;

import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Handler;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReplayConsumerBean {

    @Handler
    public Publisher<String> consume(Publisher<String> publisher) {
        return Flowable.fromPublisher(publisher)
                       .doOnNext(x -> log.info("ReplayConsumerBean got value: {}", x))
                       .flatMap(x -> Flowable.<String>error(new RuntimeException()))
                       .doOnTerminate(() -> log.info("Terminate ReplayConsumerBean"))
                       .doOnComplete(() -> log.info("Complete ReplayConsumerBean"));
    }
}
