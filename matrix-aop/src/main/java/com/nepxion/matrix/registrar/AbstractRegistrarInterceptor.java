package com.nepxion.matrix.registrar;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.MutablePropertyValues;

import com.nepxion.matrix.proxy.aop.AbstractInterceptor;

public abstract class AbstractRegistrarInterceptor extends AbstractInterceptor {

    protected MutablePropertyValues annotationValues;

    public AbstractRegistrarInterceptor(MutablePropertyValues annotationValues) {
        this.annotationValues = annotationValues;
    }

    public MutablePropertyValues getAnnotationValues() {
        return annotationValues;
    }

    public String getInterface(MethodInvocation invocation) {
        return getMethod(invocation).getDeclaringClass().getCanonicalName();
    }
}