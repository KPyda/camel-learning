package com.example.reactivestream;

import io.reactivex.Flowable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Slf4j
class EventProducer {
    private final CamelContext camelContext;

    @PostConstruct
    public void produce() {
        CamelReactiveStreamsService camel = CamelReactiveStreams.get(camelContext);
        Subscriber<String> elements = camel.streamSubscriber("producer", String.class);
        Flowable.just("Ala", "ma", "kota")
                .delay(10, TimeUnit.MILLISECONDS)
                .doOnNext(part -> log.info("Produce part of sentence: {}", part))
                .doOnSubscribe(x -> log.info("Start subscribe event producer"))
                .subscribe(elements);
    }
}
