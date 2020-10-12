package org.itranswarp.springioc;

import org.itranswarp.springioc.entity.User;
import org.itranswarp.springioc.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;

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
        System.out.println("register:" +  user.getName() + ", id:" + user.getId());
        User user1 = userService.registerbyH("lili134@example.com", "password","lisisi");

        var t = userService.getUserById(15);
        System.out.println(t.getCreateAt());
        var b = userService.getUserByIdMyBatis(17);
        System.out.println(b.getCreateAt());

        var c = userService.getUserByMyORM(19);
        System.out.println(c.getCreateAt());
        User us =  userService.insertUserORM("lili134@example.com", "password","lisisi");
        System.out.println(us.getId());

        String name = userService.geNameEmail("lili@example.com");
        System.out.println(name);
        var users=  userService.getUsers(1);
        for (var item : users){
            System.out.println(item.getId());
        }
//        var names = context.getBeanDefinitionNames();
//        for (var name : names){
//            System.out.println(name);
//        }

//        AppService appService = context.getBean(AppService.class);
//        appService.printLog();
        ((ConfigurableApplicationContext)context).close();
    }


}
