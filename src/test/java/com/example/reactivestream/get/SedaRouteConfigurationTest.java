package com.example.reactivestream.get;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

public class SedaRouteConfigurationTest extends CamelTestSupport {

    @Test
    public void testRoute() throws InterruptedException {
        // given:
        Publisher<String> stringPublisher = CamelReactiveStreams.get(context())
                .from("seda:end", String.class);

        // when
        Flowable.defer(() -> Flowable.just("First"))
                .delay(150, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .doOnNext(message -> sendBody("seda:start", message))
                .subscribe();

        // then
        Flowable.fromPublisher(stringPublisher)
                .take(1)
                .test()
                .await()
                .assertValue("First")
                .assertValueCount(1);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("seda:start")
                        .log("Get: ${body}")
                        .to("seda:end");
            }
        };
    }
}