package com.nepxion.matrix.proxy.example.simple.service;

import com.nepxion.matrix.proxy.example.simple.aop.MyAnnotation1;

@MyAnnotation1(name = "MyAnnotation1", label = "MyAnnotation1", description = "MyAnnotation1")
public interface MyService1 {

    void doA(String id);

    void doB(String id);
}