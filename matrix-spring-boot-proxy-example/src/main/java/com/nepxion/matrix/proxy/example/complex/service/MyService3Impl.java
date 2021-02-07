package com.nepxion.matrix.proxy.example.complex.service;

import org.springframework.stereotype.Service;

@Service
public class MyService3Impl implements MyService3 {

    @Override
    public void doE(String id) {
        System.out.println("doE");
    }

    @Override
    public void doF(String id) {
        System.out.println("doF");
    }
}