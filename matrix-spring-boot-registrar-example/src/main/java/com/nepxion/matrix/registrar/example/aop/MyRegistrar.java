package com.nepxion.matrix.registrar.example.aop;

import com.nepxion.matrix.registrar.AbstractRegistrar;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.MutablePropertyValues;

import java.lang.annotation.Annotation;

public class MyRegistrar extends AbstractRegistrar {

    @Override
    protected Class<? extends Annotation> getEnableAnnotationClass() {
        return EnableMyAnnotation.class;
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return MyAnnotation.class;
    }

    @Override
    protected Class<?> getBeanClass() {
        return MyRegistrarFactoryBean.class;
    }

    @Override
    protected MethodInterceptor getInterceptor(MutablePropertyValues annotationValues) {
        return new MyInterceptor(annotationValues);
    }
}