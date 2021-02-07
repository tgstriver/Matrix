package com.nepxion.matrix.proxy.example.simple.service;

import org.springframework.stereotype.Service;

import com.nepxion.matrix.proxy.example.simple.aop.MyAnnotation2;

@Service("myService2Impl")
public class MyService2Impl {

    @MyAnnotation2(name = "MyAnnotation2", label = "MyAnnotation2", description = "MyAnnotation2")
    public void doC(String id) {
        System.out.println("doC");
    }

    public void doD(String id) {
        System.out.println("doD");
    }
}