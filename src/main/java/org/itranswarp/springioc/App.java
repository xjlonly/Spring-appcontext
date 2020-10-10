package org.itranswarp.springioc;

import org.itranswarp.springioc.config.AppService;
import org.itranswarp.springioc.service.User;
import org.itranswarp.springioc.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import java.time.ZoneId;

/**
 * Hello world!
 *
 */


public class App 
{
    @SuppressWarnings("resource")
    public static void main( String[] args ) {
        //创建Spring ioc容器实例 一次性创建所有的Bean
//        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
//


        //按需创建Bean 此方法已经弃用
//        BeanFactory factory = new XmlBeanFactory(new ClassPathResource("application.xml"));
//        MailService mailService = factory.getBean(MailService.class);

        //Annotation配置注入
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        UserService userService = context.getBean(UserService.class);
        User user = userService.register("lili@example.com", "password","lili");
        System.out.println("register:" +  user.getName());
//
//        var names = context.getBeanDefinitionNames();
//        for (var name : names){
//            System.out.println(name);
//        }

//        AppService appService = context.getBean(AppService.class);
//        appService.printLog();
    }


}
