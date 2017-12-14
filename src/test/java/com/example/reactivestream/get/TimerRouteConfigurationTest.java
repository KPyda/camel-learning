package com.example.reactivestream.get;

import io.reactivex.Flowable;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TimerRouteConfigurationTest extends CamelTestSupport {

    @Test
    public void testRoute() throws InterruptedException {
        Flowable.fromPublisher(CamelReactiveStreams.get(context()).fromStream("numbers", String.class))
                .take(2)
                .test()
                .await()
                .assertValues("1", "2");
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer:clock")
                        .setBody().header(Exchange.TIMER_COUNTER)
                        .to("reactive-streams:numbers");
            }
        };
    }
}