package com.example.reactivestream.process;

import io.reactivex.Flowable;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SendToRouteConfigurationTest extends CamelTestSupport {

    @Test
    public void testRoute() throws InterruptedException {
        // given:
        CamelReactiveStreamsService camelReactiveStreamsService = CamelReactiveStreams.get(context());

        // when
        Flowable.just("First")
                .flatMap(string -> camelReactiveStreamsService.to("direct:process", string, String.class))
                .test()
                .await()
                .assertValueCount(1)
                .assertValue("First Processed");
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:process")
                        .log("Get: ${body}")
                        .process(exchange -> {
                            String body = exchange.getIn().getBody(String.class);
                            exchange.getIn().setBody(body + " Processed");
                        })
                        .log("After processed: ${body}");
            }
        };
    }
}