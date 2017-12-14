package com.example.reactivestream.process;

import io.reactivex.Flowable;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ProcessRouteConfigurationTest extends CamelTestSupport {

    @Test
    public void testRoute() throws InterruptedException {
        // given:
        CamelReactiveStreamsService camelReactiveStreamsService = CamelReactiveStreams.get(context());
        MockEndpoint mockEndpoint = getMockEndpoint("mock:end");

        // when
        camelReactiveStreamsService.process("direct:reactive", String.class, numbers ->
                Flowable.fromPublisher(numbers)
                        .map(number -> "Number: " + number)
                        .flatMap(string -> camelReactiveStreamsService.to("mock:end", string))
        );

        // then:
        mockEndpoint.expectedMessageCount(2);
        mockEndpoint.expectedBodiesReceived("Number: 1", "Number: 2");
        mockEndpoint.assertIsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("timer:clock")
                        .setBody().header(Exchange.TIMER_COUNTER)
                        .to("direct:reactive")
                        .log("Continue with Camel route... n=${body}");
            }
        };
    }
}