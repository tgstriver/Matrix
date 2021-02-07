package com.nepxion.matrix.registrar.example.service;

import com.nepxion.matrix.registrar.example.aop.MyAnnotation;

@MyAnnotation(name = "1", label = "2", description = "3")
public interface MyService3 {

    String doE(String id);

    String doF(String id);
}