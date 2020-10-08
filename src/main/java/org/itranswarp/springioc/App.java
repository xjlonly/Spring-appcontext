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
@Configuration
@ComponentScan
@PropertySource("app.properties") //表示读取classpath的app.properties
@PropertySource("smtp.properties")
public class App 
{
    @Value("${app.zone}")
    String zoneId;
    @SuppressWarnings("resource")
    public static void main( String[] args ) {
        //创建Spring ioc容器实例 一次性创建所有的Bean
//        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
//


        //按需创建Bean 此方法已经弃用
//        BeanFactory factory = new XmlBeanFactory(new ClassPathResource("application.xml"));
//        MailService mailService = factory.getBean(MailService.class);

        //Annotation配置注入
        ApplicationContext context = new AnnotationConfigApplicationContext(App.class);

        UserService userService = context.getBean(UserService.class);
        User user = userService.register("lili@example.com", "password","lili");
        System.out.println(user.getName());

        var names = context.getBeanDefinitionNames();
        for (var name : names){
            System.out.println(name);
        }

        AppService appService = context.getBean(AppService.class);
        appService.printLog();
    }

    @Bean("Z") //如果一个Bean不在我们的package中 通过在@Configuraion类中 标记为@Bean的方法创建
    @Primary //指定主注入Bean 注入时没有指定Bean名称 默认使用@Primary的Bean
    ZoneId createZoneId(){
        return ZoneId.of(zoneId);
    }

    @Bean
    @Qualifier("UTC8") //多个同类型的Bean指定别名 注入时要指定Bean名称
    ZoneId createZoneIdUTC8(){
        return ZoneId.of("UTC+08:00");
    }
}
