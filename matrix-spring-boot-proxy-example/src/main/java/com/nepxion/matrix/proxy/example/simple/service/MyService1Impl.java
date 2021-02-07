package com.nepxion.matrix.proxy.example.simple.service;

import org.springframework.stereotype.Service;

@Service("myService1Impl")
public class MyService1Impl implements MyService1 {

    @Override
    public void doA(String id) {
        System.out.println("doA");
    }

    @Override
    public void doB(String id) {
        System.out.println("doB");
    }
}