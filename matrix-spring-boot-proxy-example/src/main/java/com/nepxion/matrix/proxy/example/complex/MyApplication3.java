package com.nepxion.matrix.proxy.example.complex;

import com.nepxion.matrix.proxy.example.complex.service.MyService3;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MyApplication3 {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(MyApplication3.class, args);
        MyService3 myService3 = applicationContext.getBean(MyService3.class);
        myService3.doE("E");
        myService3.doF("F");
    }
}