package com.example;

import lombok.AllArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.function.Function;

import static lombok.AccessLevel.PROTECTED;

/**
 * Processor for beans are package scope
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
@AllArgsConstructor(access = PROTECTED)
public class BeanProcessor<T, R> implements Processor {

    private Function<T, R> transformFunction;

    public static <T, R> BeanProcessor of(Function<T, R> function) {
        return new BeanProcessor<>(function);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Exchange exchange) {
        T body = (T) exchange.getIn()
                             .getBody();
        exchange.getIn()
                .setBody(transformFunction.apply(body));
    }
}
