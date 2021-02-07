package com.nepxion.matrix.proxy.example.complex;

import com.nepxion.matrix.proxy.example.complex.service.MyService4Impl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MyApplication4 {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MyApplication4.class, args);
        MyService4Impl myService4 = applicationContext.getBean(MyService4Impl.class);
        myService4.doG("G");
        myService4.doH("H");
    }
}