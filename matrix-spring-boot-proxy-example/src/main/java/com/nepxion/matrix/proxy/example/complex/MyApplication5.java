package com.nepxion.matrix.proxy.example.complex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.nepxion.matrix.proxy.example.complex.service.MyService5Impl;

@SpringBootApplication
public class MyApplication5 {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MyApplication5.class, args);
        MyService5Impl myService5 = applicationContext.getBean(MyService5Impl.class);
        myService5.doI("E");
        myService5.doJ("F");
    }
}