package com.nepxion.matrix.proxy.example.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.nepxion.matrix.proxy.example.simple.service.MyService1;

@SpringBootApplication
public class MyApplication1 {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MyApplication1.class, args);
        MyService1 myService1 = applicationContext.getBean(MyService1.class);
        myService1.doA("A");
        myService1.doB("B");
    }
}