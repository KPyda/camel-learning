package com.example;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static rx.Observable.just;

@Slf4j
public class FlatMapTest {

    @Test
    public void doingFlatMapOnMap() {
        //given
        A a = new A();
        for (int i = 0; i < 100; i++) {
            a.map.put("key: " + i, new B());
        }

        //when
        A result = just(a)
                .flatMap(this::setupData)
                .map(bs -> a)
                .doOnNext(a1 -> log.info("I've done {}", a1))
                .toBlocking()
                .single();

        // then
        for (B resultB : result.getMap()
                               .values()) {
            Assert.assertEquals("FlatMap", resultB.getName());
        }

    }

    private Observable<List<B>> setupData(A a) {
        return Observable.from(a.getMap()
                                .values())
                         .flatMap(b -> just(b)
                                          .delay(1, TimeUnit.SECONDS)
                                          .doOnNext(b1 -> log.info("Setup name"))
                                          .doOnNext(b1 -> b1.setName("FlatMap"))
                                 )
                         .toList();
    }

    @Data
    class A {
        Map<String, B> map = new HashMap<>();
    }

    @Data
    class B {
        String name;
    }

}