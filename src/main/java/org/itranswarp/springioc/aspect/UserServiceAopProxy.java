package org.itranswarp.springioc.aspect;

import org.itranswarp.springioc.service.User;
import org.itranswarp.springioc.service.UserService;

/*
* 简单aop实现原理 Spring容器启动时自动创建注入了Aspect子类 它取代原始的UserService
*
* */
public class UserServiceAopProxy extends UserService{
    private final UserService target;
    private final LoggingAspect aspect;

    public UserServiceAopProxy(UserService target, LoggingAspect aspect) {
        this.target = target;
        this.aspect = aspect;
    }

    @Override
    public User login(String email, String password) {
        //先执行Aspect的代码
        aspect.doAccessCheck();
        //在执行UseService的逻辑
        return target.login(email, password);
    }

    @Override
    public User getUser(long id) {
        //先执行Aspect的代码
        aspect.doAccessCheck();
        return target.getUser(id);
    }

    @Override
    public User register(String email, String password, String name) {
        //先执行Aspect的代码
        aspect.doAccessCheck();
        return target.register(email, password, name);
    }
}
