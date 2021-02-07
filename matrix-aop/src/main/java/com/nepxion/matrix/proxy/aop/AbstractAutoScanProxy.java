package com.nepxion.matrix.proxy.aop;

import com.nepxion.banner.BannerConstant;
import com.nepxion.banner.Description;
import com.nepxion.banner.LogoBanner;
import com.nepxion.banner.NepxionBanner;
import com.nepxion.matrix.constant.MatrixConstant;
import com.nepxion.matrix.proxy.constant.ProxyConstant;
import com.nepxion.matrix.proxy.mode.ProxyMode;
import com.nepxion.matrix.proxy.mode.ScanMode;
import com.nepxion.matrix.proxy.util.ProxyUtil;
import com.taobao.text.Color;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAutoScanProxy extends AbstractAutoProxyCreator {

    private static final long serialVersionUID = 6827218905375993727L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAutoScanProxy.class);

    static {
        LogoBanner logoBanner = new LogoBanner(AbstractAutoScanProxy.class, "/com/nepxion/matrix/resource/logo.txt",
                "Welcome to Nepxion", 6, 5,
                new Color[]{Color.red, Color.green, Color.cyan, Color.blue, Color.yellow, Color.magenta}, true);

        NepxionBanner.show(logoBanner,
                new Description(BannerConstant.VERSION + ":", MatrixConstant.MATRIX_VERSION, 0, 1),
                new Description(BannerConstant.PLUGIN + ":", "AutoScanProxy", 0, 1),
                new Description(BannerConstant.GITHUB + ":", BannerConstant.NEPXION_GITHUB + "/Matrix", 0, 1));
    }

    // Bean名称和Bean对象关联
    private final Map<String, Object> beanMap = new HashMap<>();

    // Spring容器中哪些接口或者类需要被代理
    private final Map<String, Boolean> proxyMap = new HashMap<>();

    // Spring容器中哪些类是类代理，哪些类是通过它的接口做代理
    private final Map<String, Boolean> proxyTargetClassMap = new HashMap<>();

    // 扫描目录，如果不指定，则扫描全局。两种方式运行结果没区别，只是指定扫描目录加快扫描速度，同时可以减少缓存量
    private String[] scanPackages;

    // 通过注解确定代理
    private ProxyMode proxyMode;

    // 扫描注解后的处理
    private ScanMode scanMode;

    protected AbstractAutoScanProxy() {
        this((String[]) null);
    }

    protected AbstractAutoScanProxy(String scanPackages) {
        this(scanPackages, ProxyMode.BY_CLASS_OR_METHOD_ANNOTATION, ScanMode.FOR_CLASS_OR_METHOD_ANNOTATION);
    }

    protected AbstractAutoScanProxy(String[] scanPackages) {
        this(scanPackages, ProxyMode.BY_CLASS_OR_METHOD_ANNOTATION, ScanMode.FOR_CLASS_OR_METHOD_ANNOTATION);
    }

    protected AbstractAutoScanProxy(ProxyMode proxyMode, ScanMode scanMode) {
        this((String[]) null, proxyMode, scanMode);
    }

    protected AbstractAutoScanProxy(String scanPackages, ProxyMode proxyMode, ScanMode scanMode) {
        this(scanPackages, proxyMode, scanMode, true);
    }

    protected AbstractAutoScanProxy(String[] scanPackages, ProxyMode proxyMode, ScanMode scanMode) {
        this(scanPackages, proxyMode, scanMode, true);
    }

    protected AbstractAutoScanProxy(ProxyMode proxyMode, ScanMode scanMode, boolean exposeProxy) {
        this((String[]) null, proxyMode, scanMode, exposeProxy);
    }

    protected AbstractAutoScanProxy(String scanPackages, ProxyMode proxyMode, ScanMode scanMode, boolean exposeProxy) {
        this(StringUtils.isNotEmpty(scanPackages) ? scanPackages.trim().split(ProxyConstant.SEPARATOR) : null, proxyMode, scanMode, exposeProxy);
    }

    protected AbstractAutoScanProxy(String[] scanPackages, ProxyMode proxyMode, ScanMode scanMode, boolean exposeProxy) {
        this.scanPackages = scanPackages;
        super.setExposeProxy(exposeProxy);
        this.proxyMode = proxyMode;
        this.scanMode = scanMode;

        StringBuilder builder = new StringBuilder();
        if (ArrayUtils.isNotEmpty(scanPackages)) {
            for (int i = 0; i < scanPackages.length; i++) {
                String scanPackage = scanPackages[i];
                builder.append(scanPackage);
                if (i < scanPackages.length - 1) {
                    builder.append(ProxyConstant.SEPARATOR);
                }
            }
        }

        LOG.info("------------- Matrix Aop Information ------------");
        LOG.info("Auto scan proxy class is {}", getClass().getCanonicalName());
        LOG.info("Scan packages is {}", ArrayUtils.isNotEmpty(scanPackages) ? builder.toString() : "not set");
        LOG.info("Proxy mode is {}", proxyMode);
        LOG.info("Scan mode is {}", scanMode);
        LOG.info("Expose proxy is {}", exposeProxy);
        LOG.info("-------------------------------------------------");

        // 设定全局拦截器，可以是多个
        // 如果同时设置了全局和额外的拦截器，那么它们都同时工作，全局拦截器先运行，额外拦截器后运行
        Class<? extends MethodInterceptor>[] commonInterceptorClasses = this.getCommonInterceptors();
        String[] commonInterceptorNames = this.getCommonInterceptorNames();

        String[] interceptorNames = ArrayUtils.addAll(commonInterceptorNames, this.convertInterceptorNames(commonInterceptorClasses));
        if (ArrayUtils.isNotEmpty(interceptorNames)) {
            super.setInterceptorNames(interceptorNames);
        }
    }

    private String[] convertInterceptorNames(Class<? extends MethodInterceptor>[] commonInterceptorClasses) {
        if (ArrayUtils.isNotEmpty(commonInterceptorClasses)) {
            String[] interceptorNames = new String[commonInterceptorClasses.length];
            for (int i = 0; i < commonInterceptorClasses.length; i++) {
                Class<? extends MethodInterceptor> interceptorClass = commonInterceptorClasses[i];
                interceptorNames[i] = interceptorClass.getAnnotation(Component.class).value();
            }

            return interceptorNames;
        }

        return null;
    }

    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource targetSource) {
        boolean scanPackagesEnabled = this.scanPackagesEnabled();
        // scanPackagesEnabled=false，表示“只扫描指定目录”的方式未开启，则不会对扫描到的bean进行代理预先判断
        if (scanPackagesEnabled) {
            boolean scanPackagesContained = this.scanPackagesContained(beanClass);
            // 如果beanClass的类路径，未包含在扫描目录中，返回DO_NOT_PROXY
            if (!scanPackagesContained) {
                return DO_NOT_PROXY;
            }
        }

        // 根据Bean名称获取Bean对象
        Object bean = beanMap.get(beanName);
        // 获取最终目标类
        Class<?> targetClass;
        if (bean != null) {
            targetClass = AopProxyUtils.ultimateTargetClass(bean);
        } else {
            targetClass = beanClass;
        }

        // Spring容器扫描实现类
        if (!targetClass.isInterface()) {
            // 扫描接口（从实现类找到它的所有接口）
            if (targetClass.getInterfaces() != null) {
                for (Class<?> targetInterface : targetClass.getInterfaces()) {
                    Object[] proxyInterceptors = this.scanAndProxyForTarget(targetInterface, beanName, false);
                    if (proxyInterceptors != DO_NOT_PROXY) {
                        return proxyInterceptors;
                    }
                }
            }

            // 扫描实现类（如果接口上没找到注解， 就找实现类的注解）
            return this.scanAndProxyForTarget(targetClass, beanName, true);
        }

        return DO_NOT_PROXY;
    }

    protected Object[] scanAndProxyForTarget(Class<?> targetClass, String beanName, boolean proxyTargetClass) {
        String targetClassName = targetClass.getCanonicalName();
        Object[] interceptors = this.getInterceptors(targetClass);

        // 排除java开头的接口，例如java.io.Serializable，java.io.Closeable等，执行不被代理
        if (StringUtils.isNotEmpty(targetClassName) && !targetClassName.startsWith("java.")) {
            // 避免对同一个接口或者类扫描多次
            Boolean proxied = proxyMap.get(targetClassName);
            if (Boolean.TRUE.equals(proxied)) {
                return interceptors;
            } else {
                Object[] proxyInterceptors = null;
                switch (proxyMode) {
                    // 只通过扫描到接口名或者类名上的注解后，来确定是否要代理
                    case BY_CLASS_ANNOTATION_ONLY:
                        proxyInterceptors = this.scanAndProxyForClass(targetClass, targetClassName, beanName, interceptors, proxyTargetClass);
                        break;
                    // 只通过扫描到接口或者类方法上的注解后，来确定是否要代理
                    case BY_METHOD_ANNOTATION_ONLY:
                        proxyInterceptors = this.scanAndProxyForMethod(targetClass, targetClassName, beanName, interceptors, proxyTargetClass);
                        break;
                    // 上述两者都可以
                    case BY_CLASS_OR_METHOD_ANNOTATION:
                        Object[] classProxyInterceptors = this.scanAndProxyForClass(targetClass, targetClassName, beanName, interceptors, proxyTargetClass);
                        // 如果没有在接口或者类名上扫描到目标注解，那么扫描接口或者类的方法上的目标注解
                        Object[] methodProxyInterceptors = this.scanAndProxyForMethod(targetClass, targetClassName, beanName, interceptors, proxyTargetClass);
                        if (classProxyInterceptors != DO_NOT_PROXY || methodProxyInterceptors != DO_NOT_PROXY) {
                            proxyInterceptors = interceptors;
                        } else {
                            proxyInterceptors = DO_NOT_PROXY;
                        }
                        break;
                }

                // 是否需要代理
                proxyMap.put(targetClassName, proxyInterceptors != DO_NOT_PROXY);

                if (proxyInterceptors != DO_NOT_PROXY) {
                    // 是接口代理还是类代理，如果proxyTargetClass=true则是类代理，如果proxyTargetClass=false则是接口代理
                    proxyTargetClassMap.put(beanName, proxyTargetClass);

                    LOG.info("------------ Matrix Proxy Information -----------");
                    Class<? extends MethodInterceptor>[] commonInterceptorClasses = this.getCommonInterceptors();
                    if (ArrayUtils.isNotEmpty(commonInterceptorClasses)) {
                        LOG.info("Class [{}] is proxied by common interceptor classes [{}], proxyTargetClass={}", targetClassName, ProxyUtil.toString(commonInterceptorClasses), proxyTargetClass);
                    }

                    String[] commonInterceptorNames = this.getCommonInterceptorNames();
                    if (ArrayUtils.isNotEmpty(commonInterceptorNames)) {
                        LOG.info("Class [{}] is proxied by common interceptor beans [{}], proxyTargetClass={}", targetClassName, ProxyUtil.toString(commonInterceptorNames), proxyTargetClass);
                    }

                    if (proxyInterceptors != PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS && ArrayUtils.isNotEmpty(proxyInterceptors)) {
                        LOG.info("Class [{}] is proxied by additional interceptors [{}], proxyTargetClass={}", targetClassName, proxyInterceptors, proxyTargetClass);
                    }
                    LOG.info("-------------------------------------------------");
                }

                return proxyInterceptors;
            }
        }

        return DO_NOT_PROXY;
    }

    protected Object[] scanAndProxyForClass(Class<?> targetClass, String targetClassName,
                                            String beanName, Object[] interceptors, boolean proxyTargetClass) {
        // 判断目标注解是否标注在接口名或者类名上
        boolean proxied = false;
        Class<? extends Annotation>[] classAnnotations = this.getClassAnnotations();
        if (ArrayUtils.isNotEmpty(classAnnotations)) {
            for (Class<? extends Annotation> classAnnotation : classAnnotations) {
                if (targetClass.isAnnotationPresent(classAnnotation)) {
                    // 是否执行“注解扫描后处理”
                    if (scanMode == ScanMode.FOR_CLASS_ANNOTATION_ONLY || scanMode == ScanMode.FOR_CLASS_OR_METHOD_ANNOTATION) {
                        this.classAnnotationScanned(targetClass, classAnnotation);
                    } else {
                        // 如果“注解扫描后处理”不开启，没必要再往下执行循环，直接返回
                        return interceptors;
                    }

                    // 目标注解被扫描到，proxied赋值为true，即认为该接口或者类被代理
                    if (!proxied) {
                        proxied = true;
                    }
                }
            }
        }

        return proxied ? interceptors : DO_NOT_PROXY;
    }

    protected Object[] scanAndProxyForMethod(Class<?> targetClass, String targetClassName, String beanName, Object[] interceptors, boolean proxyTargetClass) {
        // 判断目标注解是否标注在方法上
        boolean proxied = false;
        Class<? extends Annotation>[] methodAnnotations = this.getMethodAnnotations();
        if (ArrayUtils.isNotEmpty(methodAnnotations)) {
            for (Method method : targetClass.getDeclaredMethods()) {
                for (Class<? extends Annotation> methodAnnotation : methodAnnotations) {
                    if (method.isAnnotationPresent(methodAnnotation)) {
                        // 是否执行“注解扫描后处理”
                        if (scanMode == ScanMode.FOR_METHOD_ANNOTATION_ONLY || scanMode == ScanMode.FOR_CLASS_OR_METHOD_ANNOTATION) {
                            this.methodAnnotationScanned(targetClass, method, methodAnnotation);
                        } else {
                            // 如果“注解扫描后处理”不开启，没必要再往下执行循环，直接返回
                            return interceptors;
                        }

                        // 目标注解被扫描到，proxied赋值为true，即认为该接口或者类被代理
                        if (!proxied) {
                            proxied = true;
                        }
                    }
                }
            }
        }

        return proxied ? interceptors : DO_NOT_PROXY;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Object object = super.postProcessBeforeInitialization(bean, beanName);

        // 前置扫描，把Bean名称和Bean对象关联存入Map
        boolean scanPackagesEnabled = this.scanPackagesEnabled();
        if (scanPackagesEnabled) {
            boolean scanPackagesContained = this.scanPackagesContained(bean.getClass());
            if (scanPackagesContained) {
                // 如果beanClass的类路径包含在扫描目录中，则加入beanMap
                beanMap.put(beanName, bean);
            }
        } else {
            // scanPackagesEnabled=false，表示“只扫描指定目录”的方式未开启，则所有扫描到的bean都加入beanMap
            beanMap.put(beanName, bean);
        }

        return object;
    }

    @Override
    protected boolean shouldProxyTargetClass(Class<?> beanClass, String beanName) {
        // 设置不同场景下的接口代理，还是类代理
        Boolean proxyTargetClass = proxyTargetClassMap.get(beanName);
        if (proxyTargetClass != null) {
            return proxyTargetClass;
        }

        return super.shouldProxyTargetClass(beanClass, beanName);
    }

    /**
     * 是否是“只扫描指定目录”的方式
     *
     * @return
     */
    protected boolean scanPackagesEnabled() {
        return ArrayUtils.isNotEmpty(scanPackages);
    }

    /**
     * 指定的beanClass是否包含在扫描目录中
     *
     * @param beanClass
     * @return
     */
    protected boolean scanPackagesContained(Class<?> beanClass) {
        for (String scanPackage : scanPackages) {
            if (StringUtils.isNotEmpty(scanPackage)) {
                // beanClassName有时候会为null
                String beanClassName = beanClass.getCanonicalName();
                if (StringUtils.isNotEmpty(beanClassName)) {
                    if (beanClassName.startsWith(scanPackage)
                            || beanClassName.contains(ProxyConstant.JDK_PROXY_NAME_KEY)
                            || beanClassName.contains(ProxyConstant.CGLIB_PROXY_NAME_KEY)) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * 获取切面拦截类的方式
     * 1. 根据targetClass从额外拦截类列表中去取（接口代理targetClass是接口类，类代理targetClass是类）
     * 2. 如果从额外拦截类列表中没取到，就从全局拦截类列表中去取（PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS）
     * 3. 如果从全局拦截类列表中没取到，就不代理（DO_NOT_PROXY）
     *
     * @param targetClass
     * @return
     */
    protected Object[] getInterceptors(Class<?> targetClass) {
        Object[] interceptors = this.getAdditionalInterceptors(targetClass);
        if (ArrayUtils.isNotEmpty(interceptors)) {
            return interceptors;
        }

        Class<? extends MethodInterceptor>[] commonInterceptorClasses = this.getCommonInterceptors();
        String[] commonInterceptorNames = this.getCommonInterceptorNames();
        if (ArrayUtils.isNotEmpty(commonInterceptorClasses) || ArrayUtils.isNotEmpty(commonInterceptorNames)) {
            return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
        }

        return DO_NOT_PROXY;
    }

    /**
     * 返回具有调用拦截的全局切面实现类，拦截类必须实现MethodInterceptor接口, 可以多个，通过@Component("xxx")方式寻址
     * 如果返回null， 全局切面代理关闭
     *
     * @return
     */
    protected abstract Class<? extends MethodInterceptor>[] getCommonInterceptors();

    /**
     * 返回具有调用拦截的全局切面实现类的Bean名，拦截类必须实现MethodInterceptor接口, 可以多个
     * 如果返回null， 全局切面代理关闭
     *
     * @return
     */
    protected abstract String[] getCommonInterceptorNames();

    /**
     * 返回额外的拦截类实例列表，拦截类必须实现MethodInterceptor接口，分别对不同的接口或者类赋予不同的拦截类，可以多个
     * 如果返回null， 额外切面代理关闭
     *
     * @param targetClass
     * @return
     */
    protected abstract MethodInterceptor[] getAdditionalInterceptors(Class<?> targetClass);

    /**
     * 返回接口名或者类名上的注解列表，可以多个, 如果接口名或者类名上存在一个或者多个该列表中的注解，即认为该接口或者类需要被代理和扫描
     * 如果返回null，则对列表中的注解不做代理和扫描
     *
     * @return
     */
    protected abstract Class<? extends Annotation>[] getClassAnnotations();

    /**
     * 返回接口或者类的方法名上的注解，可以多个，如果接口或者类中方法名上存在一个或者多个该列表中的注解，即认为该接口或者类需要被代理和扫描
     * 如果返回null，则对列表中的注解不做代理和扫描
     *
     * @return
     */
    protected abstract Class<? extends Annotation>[] getMethodAnnotations();

    /**
     * 一旦指定的接口或者类名上的注解被扫描到，将会触发该方法
     *
     * @param targetClass
     * @param classAnnotation
     */
    protected abstract void classAnnotationScanned(Class<?> targetClass, Class<? extends Annotation> classAnnotation);

    /**
     * 一旦指定的接口或者类的方法名上的注解被扫描到，将会触发该方法
     *
     * @param targetClass
     * @param method
     * @param methodAnnotation
     */
    protected abstract void methodAnnotationScanned(Class<?> targetClass, Method method, Class<? extends Annotation> methodAnnotation);
}