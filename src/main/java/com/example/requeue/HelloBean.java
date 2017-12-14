package com.example.requeue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by lguz on 27.12.16.
 */
@Component
public class HelloBean {

    private Logger log = LoggerFactory.getLogger(HelloBean.class);

    public String hello(String s) {
        log.info("Hello {}, thread: {}", s, Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (false)
            throw new RuntimeException();
        log.info("Awake Hello {}, thread: {}", s, Thread.currentThread().getName());
        return "Hello " + s;
    }
}
