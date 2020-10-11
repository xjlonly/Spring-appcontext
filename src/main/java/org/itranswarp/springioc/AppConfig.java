package org.itranswarp.springioc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.SessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.time.ZoneId;
import java.util.Properties;

@MapperScan("org.itranswarp.springioc.mybatis")
@Configuration
@ComponentScan
@PropertySource("app.properties") //表示读取classpath的app.properties
@PropertySource("smtp.properties")
@PropertySource("jdbc.properties")
@EnableTransactionManagement // 启用声明式事物 声明了此注解后 不必额外添加@EnableAspectJAutoProxy
//@EnableAspectJAutoProxy
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
    @Bean
    @Primary
    PlatformTransactionManager createTxManager(@Autowired @Qualifier("hsqldb") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }


    //Hibernate

    @Bean
    @Qualifier("hf")
    LocalSessionFactoryBean createSessionFactoryBean(@Autowired @Qualifier("hsqldb") DataSource dataSource){
        var props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto", "update");//生产环境不要使用
        props.setProperty("hibernate.dialect","org.hibernate.dialect.HSQLDialect");
        props.setProperty("hibernate.show_sql","true");
        props.setProperty("hibernate.allow_update_outside_transaction", "true");
        var sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        //扫描指定的package获取所以的entity class;
        sessionFactoryBean.setPackagesToScan("org.itranswarp.springioc.entity");
        sessionFactoryBean.setHibernateProperties(props);
        return sessionFactoryBean;
    }
    @Bean
    @Qualifier("hibernatetemp")
    HibernateTemplate createHibernateTemplate(@Autowired @Qualifier("hf") SessionFactory sessionFactory){
        return  new HibernateTemplate(sessionFactory);
    }

    @Bean
    @Qualifier("hibernate")
    PlatformTransactionManager createHTxManager(@Autowired @Qualifier("hf") SessionFactory sessionFactory){
        return  new HibernateTransactionManager(sessionFactory);
    }


    //集成JPA
    @Bean
    @Primary
    LocalContainerEntityManagerFactoryBean createEntityManagerFactory(@Autowired @Qualifier("hsqldb") DataSource dataSource){
        var entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        //扫描指定包的entity class
        entityManagerFactoryBean.setPackagesToScan("org.itranswarp.springioc.entity");
        //指定JPA的指定提供商是Hibernate
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

        //设定特定提供商自己的配置
        var props = new Properties();
        props.setProperty("hibernate.hbm2ddl.auto", "update");//生产环境不要使用
        props.setProperty("hibernate.dialect","org.hibernate.dialect.HSQLDialect");
        props.setProperty("hibernate.show_sql","true");
        props.setProperty("hibernate.allow_update_outside_transaction", "true");
        entityManagerFactoryBean.setJpaProperties(props);
        return  entityManagerFactoryBean;
    }

    @Bean
    @Qualifier("jpa")
    PlatformTransactionManager createJpaTxManager(@Autowired EntityManagerFactory entityManagerFactory){
        return  new JpaTransactionManager(entityManagerFactory);
    }

    //Mybatis
    @Bean
    @Qualifier("mybatis")
    SqlSessionFactoryBean createSqlSessionFactoryBean(@Autowired @Qualifier("hsqldb") DataSource dataSource){
        var sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }

    //MyBatis可以直接使用Spring管理的声明式事务 创建事务管理器和使用JDBC是一样的
//    @Bean
//    PlatformTransactionManager createTxManager(@Autowired @Qualifier("hsqldb") DataSource dataSource){
//        return new DataSourceTransactionManager(dataSource);
//    }
}
