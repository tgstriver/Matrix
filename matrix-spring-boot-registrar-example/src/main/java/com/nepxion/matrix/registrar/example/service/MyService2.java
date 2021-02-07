package com.nepxion.matrix.registrar.example.service;

import com.nepxion.matrix.registrar.example.aop.MyAnnotation;

@MyAnnotation(name = "x", label = "y", description = "z")
public interface MyService2 {

    String doC(String id);

    String doD(String id);
}