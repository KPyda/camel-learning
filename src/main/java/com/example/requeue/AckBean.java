package com.example.requeue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
public class AckBean implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("Jestem w AckBean i rzucam RuntimeException");
        throw new RuntimeException();

    }
}
