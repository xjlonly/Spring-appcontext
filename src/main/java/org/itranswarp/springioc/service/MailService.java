package org.itranswarp.springioc.service;

import org.itranswarp.springioc.config.SmtpConfig;
import org.itranswarp.springioc.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)//每次都会返回新实例
public class MailService {
    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }
    @Autowired(required = false) //找到类型ZoneId的Bean就注入，找不到就使用默认值
    @Qualifier("Z")
    private ZoneId zoneId = ZoneId.systemDefault();

    @Autowired
    private SmtpConfig smtpConfig; //直接注入持有配置的Bean

    @Value("#{smtpConfig.host}") //从持有配置的bean中注入
    private String host;

    public String getTime(){
        return ZonedDateTime.now(this.zoneId).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public void sendLoginMail(User user){
        System.err.println(String.format("Hi, %s! You are logged in at %s", user.getName(), getTime()));
    }

    public void sendRegistrationMail(User user){
        System.err.println(String.format("Welcome, %s", user.getName()));
    }
    @PostConstruct //初始化
    public void  init(){
        System.out.println("Init mail service with zoneId = "+ this.zoneId);
    }
    @PreDestroy //销毁
    public void shutdown(){
        System.out.println("Shutdown mail service");
    }

}
