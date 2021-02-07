package com.nepxion.matrix.proxy.example.complex.service;

import org.springframework.stereotype.Service;

import com.nepxion.matrix.proxy.example.complex.aop.MyAnnotation4;
import com.nepxion.matrix.proxy.example.complex.aop.MyAnnotation5;
import com.nepxion.matrix.proxy.example.complex.aop.MyAnnotation6;
import com.nepxion.matrix.proxy.example.complex.aop.MyAnnotation7;

@Service
@MyAnnotation4(name = "MyAnnotation4", label = "MyAnnotation4", description = "MyAnnotation4")
public class MyService4Impl {

    @MyAnnotation5(name = "MyAnnotation5", label = "MyAnnotation5", description = "MyAnnotation5")
    public void doG(@MyAnnotation7(name = "MyAnnotation7", label = "MyAnnotation7", description = "MyAnnotation7") String id) {
        System.out.println("doG");
    }

    @MyAnnotation6(name = "MyAnnotation6", label = "MyAnnotation6", description = "MyAnnotation6")
    public void doH(String id) {
        System.out.println("doH");
    }
}