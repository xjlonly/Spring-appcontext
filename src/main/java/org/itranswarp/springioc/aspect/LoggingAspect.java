package org.itranswarp.springioc.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
/**
 * Spring 对接口类型使用JDK动态代理
 * 对普通类使用CGLIB创建子类，
 * 如果一个Bean的class是final，Spring将无法为其创建子类
 *
 * AOP避坑指南 Spring通过CGLIB创建的代理类，不会初始化代理类自身继承的任何成员变量，包括final类型的成员变量
 * 访问被注入的Bean时 总是调用方法而非直接访问字段
 * 代理类无法覆写final方法
 */

@Aspect
@Component
public class LoggingAspect {

    /*
    *
    * */
    //Spring使用CGLIB动态创建子类参考UserServiceAopProxy  以动态代理方式织入业务方法中
    @Before("execution(public * org.itranswarp.springioc.service.UserService.*(..))")//以特定前缀装配AOP
    public void doAccessCheck(){
        System.err.println("[Before] do access check...");
    }

    @Around("execution(public * org.itranswarp.springioc.service.MailService.*(..))")
    public Object doLogging(ProceedingJoinPoint pjp) throws Throwable{
        System.err.println("[Around] start" + pjp.getSignature());
        Object retVal = pjp.proceed();
        System.err.println("[Around] done" + pjp.getSignature());
        return retVal;
    }

    @Around("@annotation(metricTime)") //以注解方式装配AOP 通常采用此方式
    public Object metric(ProceedingJoinPoint joinPoint, MetricTime metricTime) throws Throwable{
        String name = metricTime.value();
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        }finally {
            long t = System.currentTimeMillis() - start;
            System.err.println("[Metrics]" + name + ": " + t + "ms");
        }
    }


    @Before("@annotation(xxx)") //以注解方式装配AOP 通常采用此方式
    public void metric1(MetricTime xxx) throws Throwable{
        System.err.println("[Metrics1] before");
    }
}
