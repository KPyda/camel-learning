package com.example.reactivestream;

import io.reactivex.Observable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@AllArgsConstructor
@Slf4j
class NumberHandler {

    private final CamelContext camelContext;

    @PostConstruct
    public void handle() {
        CamelReactiveStreamsService camel = CamelReactiveStreams.get(camelContext);
        Publisher<Integer> numbers = camel.fromStream("numbers", Integer.class);
        Observable.fromPublisher(numbers)
                  .doOnNext(number -> log.info("I got number: {}", number))
                  .subscribe();
    }
}
