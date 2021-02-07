package com.nepxion.matrix.proxy.example.complex.service;

import com.nepxion.matrix.proxy.example.complex.aop.MyAnnotation3;
import com.nepxion.matrix.proxy.example.complex.aop.MyAnnotation4;
import com.nepxion.matrix.proxy.example.complex.aop.MyAnnotation5;
import com.nepxion.matrix.proxy.example.complex.aop.MyAnnotation6;
import com.nepxion.matrix.proxy.example.complex.aop.MyAnnotation7;

@MyAnnotation3(name = "MyAnnotation3", label = "MyAnnotation3", description = "MyAnnotation3")
@MyAnnotation4(name = "MyAnnotation4", label = "MyAnnotation4", description = "MyAnnotation4")
public interface MyService3 {

    @MyAnnotation5(name = "MyAnnotation5", label = "MyAnnotation5", description = "MyAnnotation5")
    void doE(@MyAnnotation7(name = "MyAnnotation7", label = "MyAnnotation7", description = "MyAnnotation7") String id);

    @MyAnnotation5(name = "MyAnnotation5", label = "MyAnnotation5", description = "MyAnnotation5")
    @MyAnnotation6(name = "MyAnnotation6", label = "MyAnnotation6", description = "MyAnnotation6")
    void doF(String id);
}