package com.nepxion.matrix.registrar.example.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary // 优先Bean注入，用来代替MyService3后置切面拦截调用
public class MyService3Impl implements MyService3 {

    @Override
    public String doE(String id) {
        return "直接返回 " + id;
    }

    @Override
    public String doF(String id) {
        return "直接返回 " + id;
    }
}