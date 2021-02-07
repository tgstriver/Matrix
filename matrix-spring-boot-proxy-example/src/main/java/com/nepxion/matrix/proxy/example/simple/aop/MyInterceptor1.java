package com.nepxion.matrix.proxy.example.simple.aop;

import com.nepxion.matrix.proxy.aop.AbstractInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component("myInterceptor1")
public class MyInterceptor1 extends AbstractInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String proxyClassName = super.getProxyClassName(invocation);
        Object[] arguments = super.getArguments(invocation);
        String proxiedClassName = super.getProxiedClassName(invocation);
        Class<?>[] proxiedInterfaces = super.getProxiedInterfaces(invocation);
        Annotation[] classAnnotations = super.getProxiedClassAnnotations(invocation);
        String methodName = super.getMethodName(invocation);
        Annotation[] methodAnnotations = super.getMethodAnnotations(invocation);
        String[] parameterNames = super.getMethodParameterNames(invocation);

        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println("My Interceptor 1 :");
        System.out.println("   proxyClassName=" + proxyClassName);
        System.out.println("   className=" + proxiedClassName);
        System.out.println("   classAnnotations=");
        for (Annotation classAnnotation : classAnnotations) {
            System.out.println("      " + classAnnotation.toString());
        }

        if (proxiedInterfaces != null) {
            for (Class<?> proxiedInterface : proxiedInterfaces) {
                System.out.println("   interfaceName=" + proxiedInterface.getCanonicalName());
                System.out.println("   interfaceAnnotations=");
                for (Annotation interfaceAnnotation : proxiedInterface.getAnnotations()) {
                    System.out.println("      " + interfaceAnnotation.toString());
                }
            }
        }

        System.out.println("   methodName=" + methodName);
        System.out.println("   methodAnnotations=");
        for (Annotation methodAnnotation : methodAnnotations) {
            System.out.println("      " + methodAnnotation.toString());
        }

        System.out.println("   arguments=");
        for (Object argument : arguments) {
            System.out.println("      " + argument.toString());
        }

        if (ArrayUtils.isNotEmpty(parameterNames)) {
            System.out.println("   parameterNames=");
            for (String parameterName : parameterNames) {
                System.out.println("      " + parameterName);
            }
        }
        System.out.println("------------------------------------------------------------------------------------------");

        return invocation.proceed();
    }
}