package com.nepxion.matrix.proxy.example.complex.aop;

import com.nepxion.matrix.proxy.aop.AbstractAutoScanProxy;
import com.nepxion.matrix.proxy.example.complex.service.MyService3;
import com.nepxion.matrix.proxy.example.complex.service.MyService4Impl;
import com.nepxion.matrix.proxy.mode.ProxyMode;
import com.nepxion.matrix.proxy.mode.ScanMode;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Component("myAutoScanProxy")
public class MyAutoScanProxy extends AbstractAutoScanProxy {

    private static final long serialVersionUID = -481395242918857264L;

    private static final String[] SCAN_PACKAGES = {"com.nepxion.matrix.proxy.example.complex"};

    @SuppressWarnings("rawtypes")
    private Class[] commonInterceptorClasses;

    @SuppressWarnings("rawtypes")
    private Class[] classAnnotations;

    @SuppressWarnings("rawtypes")
    private Class[] methodAnnotations;

    @Autowired
    private MyInterceptor3 myInterceptor3;

    private MethodInterceptor[] myInterceptor3Array;

    @Autowired
    private MyInterceptor4 myInterceptor4;

    private MethodInterceptor[] myInterceptor4Array;

    // 可以设定多个全局拦截器，也可以设定多个额外拦截器；可以设定拦截触发由全局拦截器执行，还是由额外拦截器执行
    // 如果同时设置了全局和额外的拦截器，那么它们都同时工作，全局拦截器先运行，额外拦截器后运行
    public MyAutoScanProxy() {
        // SCAN_PACKAGES                           扫描目录，如果不指定，则扫描全局。两种方式运行结果没区别，只是指定扫描目录会加快扫描速度，同时可以减少缓存量
        // ProxyMode.BY_CLASS_OR_METHOD_ANNOTATION 对全部注解都进行代理
        // ProxyMode.BY_CLASS_ANNOTATION_ONLY      只代理类或者接口名上的注解
        // ProxyMode.BY_METHOD_ANNOTATION_ONLY     只代理方法上的注解
        // ScanMode.FOR_CLASS_OR_METHOD_ANNOTATION 对全部注解都进行扫描
        // ScanMode.FOR_CLASS_ANNOTATION_ONLY      只扫描类或者接口名上的注解
        // ScanMode.FOR_METHOD_ANNOTATION_ONLY     只扫描方法上的注解
        super(SCAN_PACKAGES, ProxyMode.BY_CLASS_OR_METHOD_ANNOTATION, ScanMode.FOR_CLASS_OR_METHOD_ANNOTATION);
    }

    /**
     * 返回具有调用拦截的全局切面实现类，拦截类必须实现MethodInterceptor接口, 可以多个
     * 如果返回null， 全局切面代理关闭
     *
     * @return
     */
    @Override
    protected Class<? extends MethodInterceptor>[] getCommonInterceptors() {
        if (commonInterceptorClasses == null) {
            // 延迟加载模式，避免重复构造Class数组
            commonInterceptorClasses = new Class[]{MyInterceptor3.class, MyInterceptor4.class};
        }
        return null;
    }

    @Override
    protected String[] getCommonInterceptorNames() {
        return null;
    }

    /**
     * 返回额外的拦截类实例列表，拦截类必须实现MethodInterceptor接口，分别对不同的接口或者类赋予不同的拦截类，可以多个
     * 如果返回null，额外切面代理关闭
     *
     * @param targetClass
     * @return
     */
    @Override
    protected MethodInterceptor[] getAdditionalInterceptors(Class<?> targetClass) {
        // 由int值来表示使用策略
        int strategy = 3;

        if (strategy == 1) {
            // 使用策略1：根据接口或者类决定选择哪个切面代理
            // 例如下面示例中，如果所代理的接口是MyService3，执行myInterceptor3切面拦截
            if (targetClass == MyService3.class) {
                return this.getMyInterceptor3Array();
                // 例如下面示例中，如果所代理的类是MyService4Impl，执行myInterceptor4切面拦截
            } else if (targetClass == MyService4Impl.class) {
                return this.getMyInterceptor4Array();
            }
        } else if (strategy == 2) {
            // 使用策略2：根据接口或者类头部的注解决定选择哪个切面代理
            // 例如下面示例中，如果所代理的接口或者类头部“只要”出现MyAnnotation3，所有方法都执行myInterceptor3切面拦截
            MyAnnotation3 myAnnotation3 = AnnotationUtils.findAnnotation(targetClass, MyAnnotation3.class);
            if (myAnnotation3 != null) {
                return this.getMyInterceptor3Array();
            }
        } else if (strategy == 3) {
            // 使用策略3：根据接口或者类的方法注解决定选择哪个切面代理
            // 例如下面示例中，如果所代理的接口或者类的方法中“只要”出现MyAnnotation5，所有方法都执行myInterceptor3切面拦截；
            // “只要”出现MyAnnotation6，所有方法都执行myInterceptor4切面拦截
            Method[] methods = targetClass.getDeclaredMethods();
            for (Method method : methods) {
                MyAnnotation5 myAnnotation5 = AnnotationUtils.findAnnotation(method, MyAnnotation5.class);
                if (myAnnotation5 != null) {
                    return this.getMyInterceptor3Array();
                }

                MyAnnotation6 myAnnotation6 = AnnotationUtils.findAnnotation(method, MyAnnotation6.class);
                if (myAnnotation6 != null) {
                    return this.getMyInterceptor4Array();
                }
            }
        }

        return null;
    }

    private MethodInterceptor[] getMyInterceptor3Array() {
        if (myInterceptor3Array == null) {
            // 延迟加载模式，避免重复构造Class数组
            myInterceptor3Array = new MethodInterceptor[]{myInterceptor3};
        }
        return myInterceptor3Array;
    }

    private MethodInterceptor[] getMyInterceptor4Array() {
        if (myInterceptor4Array == null) {
            // 延迟加载模式，避免重复构造Class数组
            myInterceptor4Array = new MethodInterceptor[]{myInterceptor4};
        }
        return myInterceptor4Array;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends Annotation>[] getClassAnnotations() {
        // 返回接口名或者类名上的注解列表，可以多个, 如果接口名或者类名上存在一个或者多个该列表中的注解，即认为该接口或者类需要被代理和扫描
        // 如果返回null，则对列表中的注解不做代理和扫描
        // 例如下面示例中，一旦你的接口或者类名出现MyAnnotation3或者MyAnnotation4，则所在的接口或者类将被代理和扫描
        if (classAnnotations == null) {
            // 延迟加载模式，避免重复构造Class数组
            classAnnotations = new Class[]{MyAnnotation3.class, MyAnnotation4.class};
        }
        return classAnnotations;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends Annotation>[] getMethodAnnotations() {
        // 返回接口或者类的方法名上的注解，可以多个，如果接口或者类中方法名上存在一个或者多个该列表中的注解，即认为该接口或者类需要被代理和扫描
        // 如果返回null，则对列表中的注解不做代理和扫描
        // 例如下面示例中，一旦你的方法名上出现MyAnnotation5或者MyAnnotation6，则该方法所在的接口或者类将被代理和扫描
        if (methodAnnotations == null) {
            // 延迟加载模式，避免重复构造Class数组
            methodAnnotations = new Class[]{MyAnnotation5.class, MyAnnotation6.class};
        }
        return methodAnnotations;
        // return null;
    }

    @Override
    protected void classAnnotationScanned(Class<?> targetClass, Class<? extends Annotation> classAnnotation) {
        // 一旦指定的接口或者类名上的注解被扫描到，将会触发该方法
        System.out.println("Class annotation scanned, targetClass=" + targetClass + ", classAnnotation=" + classAnnotation);
    }

    @Override
    protected void methodAnnotationScanned(Class<?> targetClass, Method method, Class<? extends Annotation> methodAnnotation) {
        // 一旦指定的接口或者类的方法名上的注解被扫描到，将会触发该方法
        System.out.println("Method annotation scanned, targetClass=" + targetClass + ", method=" + method + ", methodAnnotation=" + methodAnnotation);
    }
}