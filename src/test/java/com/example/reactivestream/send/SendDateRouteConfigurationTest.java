package com.example.reactivestream.send;

import io.reactivex.Flowable;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SendDateRouteConfigurationTest extends CamelTestSupport {

    @Test
    public void testRoute() throws InterruptedException {
        // given:
        CamelReactiveStreamsService camelReactiveStreamsService = CamelReactiveStreams.get(context());
        MockEndpoint mockEndpoint = getMockEndpoint("mock:end");

        // when
        Flowable.just("First")
                .subscribe(camelReactiveStreamsService.streamSubscriber("elements", String.class));

        // then:
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived("First");
        mockEndpoint.assertIsSatisfied();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("reactive-streams:elements")
                        .log("Get: ${body}")
                        .to("mock:end");
            }
        };
    }
}