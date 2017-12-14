package com.example.reactivestream;

import io.reactivex.Flowable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@AllArgsConstructor
@Slf4j
class EventConsumer {
    private final CamelContext camelContext;

    @PostConstruct
    public void consume() {
        CamelReactiveStreamsService camel = CamelReactiveStreams.get(camelContext);
        Flowable.fromPublisher(camel.from("seda:reactive-stream-event-consumer", String.class))
                .subscribe(names -> log.info("Part of sentence: {}", names));
    }

}
