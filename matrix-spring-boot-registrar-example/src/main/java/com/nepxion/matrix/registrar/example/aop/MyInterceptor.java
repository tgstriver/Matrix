package com.nepxion.matrix.registrar.example.aop;

import com.nepxion.matrix.registrar.AbstractRegistrarInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.MutablePropertyValues;

public class MyInterceptor extends AbstractRegistrarInterceptor {

    public MyInterceptor(MutablePropertyValues annotationValues) {
        super(annotationValues);
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
        System.out.println("---------------------代理信息---------------------");
        String interfaze = super.getInterface(invocation);
        String methodName = super.getMethodName(invocation);
        Object[] arguments = super.getArguments(invocation);

        System.out.println("Interface=" + interfaze + ", methodName=" + methodName + ", arguments=" + arguments[0]);

        Class<?> interfaceClass = (Class<?>) annotationValues.get("interfaze");
        String name = annotationValues.get("name").toString();
        String label = annotationValues.get("label").toString();
        String description = annotationValues.get("description").toString();

        System.out.println("Interface class=" + interfaceClass + ", annotation:name=" + name + ", label=" + label + ", description=" + description);
        System.out.println("-------------------------------------------------");

        // 实现业务代码

        return "代理返回 " + arguments[0];
    }
}