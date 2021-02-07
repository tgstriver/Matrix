package com.nepxion.matrix.proxy.example.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.nepxion.matrix.proxy.example.simple.service.MyService2Impl;

@SpringBootApplication
public class MyApplication2 {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MyApplication2.class, args);
        MyService2Impl myService2 = applicationContext.getBean(MyService2Impl.class);
        myService2.doC("C");
        myService2.doD("D");
    }
}