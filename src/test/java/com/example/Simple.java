package com.example;

import lombok.Value;

@Value(staticConstructor = "of")
public class Simple {
    private String name;
    private int value;
}