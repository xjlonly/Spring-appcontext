package org.itranswarp.springioc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.ZoneId;

@Configuration
@ComponentScan
@PropertySource("app.properties") //表示读取classpath的app.properties
@PropertySource("smtp.properties")
@PropertySource("jdbc.properties")
@EnableAspectJAutoProxy
public class AppConfig {
    @Value("${app.zone}")
    String zoneId;

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


    //Jdbc 配置开始
    @Value("${jdbc.url}")
    String jdbcUrl;

    @Value("${jdbc.username}")
    String userName;

    @Value("${jdbc.password}")
    String password;

    @Bean
    @Qualifier("hsqldb")
    DataSource createDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(userName);
        config.setPassword(password);
        config.addDataSourceProperty("autoCommit", true);
        config.addDataSourceProperty("connectionTimeout", 10);
        config.addDataSourceProperty("idleTimeout", 60);
        return  new HikariDataSource(config);
    }

    @Bean
    JdbcTemplate createJdbcTemplate(@Autowired @Qualifier("hsqldb") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
