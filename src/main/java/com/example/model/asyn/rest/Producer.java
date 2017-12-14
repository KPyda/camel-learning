package com.example.model.asyn.rest;

import io.reactivex.Observable;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/producers")
class Producer {

    @GetMapping
    String produce() {
        return "produce " + LocalDateTime.now();
    }
}

@RestController
@RequestMapping("/consumers")
class Consumer {

    private final AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate();

    @GetMapping
    DeferredResult<String> consume() {
        DeferredResult<String> result = new DeferredResult<>();

        Observable.fromFuture(asyncRestTemplate.getForEntity("http://localhost:8080/producers", String.class))
                  .map(HttpEntity::getBody)
                  .subscribe(result::setResult);

        return result;
    }
}