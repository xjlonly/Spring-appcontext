package org.itranswarp.springioc.myorm;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.Entity;
import java.util.*;

/*
* 设计简单的ORM
* */
public class DbTemplate {

    private JdbcTemplate jdbcTemplate;
    //自动扫描包下entity 存储相关属性
    private Map<Class<?>, Mapper<?>> classMapping;

    public DbTemplate(JdbcTemplate jdbcTemplate, String basePackage){
        this.jdbcTemplate = jdbcTemplate;
        //扫描包下所有entity
        List<Class<?>> classes = scanEntities(basePackage);
        Map<Class<?>, Mapper<?>> mapping = new HashMap<>();
        try {
            for(Class<?> clazz : classes){
                System.out.println("Found class:" + clazz.getName());
                Mapper<?> mapper = new Mapper<>(clazz);
                mapping.put(clazz, mapper);
            }
        }
        catch (Exception e){
            throw new RuntimeException();
        }
        this.classMapping = mapping;
    }

    @SuppressWarnings("unchecked")
    <T> Mapper<T> getMapper(Class<T> clazz){
            Mapper<T> mapper = (Mapper<T>)this.classMapping.get(clazz);
            if(mapper == null){
                throw new RuntimeException("Target class is not a registered entity: " + clazz.getName());
            }
            return mapper;
    }

    private List<Class<?>> scanEntities(String basePackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
        List<Class<?>> classes = new ArrayList<>();
        Set<BeanDefinition> beans = provider.findCandidateComponents(basePackage);
        for(BeanDefinition bean : beans){
            try {
                classes.add(Class.forName(bean.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException();
            }
        }
        return  classes;
    }
}
