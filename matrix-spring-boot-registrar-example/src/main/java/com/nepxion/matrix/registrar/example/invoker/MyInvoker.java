package com.nepxion.matrix.registrar.example.invoker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nepxion.matrix.registrar.example.service.MyService1;
import com.nepxion.matrix.registrar.example.service.MyService2;
import com.nepxion.matrix.registrar.example.service.MyService3;

@Component
public class MyInvoker {

    @Autowired
    private MyService1 myService1;

    @Autowired
    private MyService2 myService2;

    @Autowired
    private MyService3 myService3;

    public String invokeMyService1() {
        return myService1.doA("A");
    }

    public String invokeMyService2() {
        return myService2.doC("C");
    }

    public String invokeMyService3() {
        return myService3.doE("E");
    }
}