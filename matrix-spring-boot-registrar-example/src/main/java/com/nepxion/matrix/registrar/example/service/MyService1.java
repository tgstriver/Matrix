package com.nepxion.matrix.registrar.example.service;

import com.nepxion.matrix.registrar.example.aop.MyAnnotation;

@MyAnnotation(name = "a", label = "b", description = "c")
public interface MyService1 {

    String doA(String id);

    String doB(String id);
}