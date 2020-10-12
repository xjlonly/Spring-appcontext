package org.itranswarp.springioc.myorm;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/*
* 设计简单的ORM
* */

public class DbTemplate {

     final JdbcTemplate jdbcTemplate;
    //自动扫描包下entity 存储相关属性
    private final Map<Class<?>, Mapper<?>> classMapping;

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
    public <T> T get(Class<T> clazz, Object id){
        T t = fetch(clazz, id);
        if(t == null){
            throw new EntityNotFoundException(clazz.getSimpleName());
        }
        return t;
    }
    public <T> void update(T bean){
        try{
            Mapper<?> mapper = getMapper(bean.getClass());
            Object[] args = new Object[mapper.updatableProperties.size() + 1];
            int n = 0;
            for(AccessibleProperty prop : mapper.updatableProperties){
                args[n] = prop.getter.invoke(bean);
                n++;
            }
            args[n] = mapper.id.getter.invoke(bean);
            System.out.println("SQL:" + mapper.updateSQL);
            jdbcTemplate.update(mapper.updateSQL, args);

        }catch (ReflectiveOperationException e){
            throw  new PersistenceException(e);
        }
    }

    /**
     * insert object
     * @param bean
     * @param <T>
     */
    public <T> void insert(T bean){
        try{
            int rows;
            final  Mapper<?> mapper = getMapper(bean.getClass());
            Object[] args = new Object[mapper.insertableProperties.size()];
            int n = 0;
            for(AccessibleProperty prop : mapper.insertableProperties){
                args[n] = prop.getter.invoke(bean);
                n++;
            }
            System.out.println("SQL: " + mapper.insertSQL);
            if(mapper.id.isIdentityId()){
                KeyHolder keyHolder = new GeneratedKeyHolder();
                rows = jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(mapper.insertSQL, Statement.RETURN_GENERATED_KEYS);
                    for(int i =0; i < args.length; i++){
                        ps.setObject(i +1, args[i]);
                    }
                    return ps;
                }, keyHolder);
                if(rows ==1){
                    mapper.id.setter.invoke(bean, keyHolder.getKey());
                }
            }else{
                rows = jdbcTemplate.update(mapper.insertSQL,args);
            }
        }catch (ReflectiveOperationException e){
            throw new PersistenceException(e);
        }

    }
    /*
    * */
    public <T> void delete(Class<?>clazz, Object id){
        Mapper<?> mapper = getMapper(clazz);
        System.out.println("SQL:" + mapper.deleteSQL);
        jdbcTemplate.update(mapper.deleteSQL,id);
    }

    public <T> T fetch(Class<T> clazz, Object id){
        Mapper<T> mapper = getMapper(clazz);
        System.out.println("Sql : " + mapper.selectSQL);
        List<T> list = (List<T>) jdbcTemplate.query(mapper.selectSQL, new Object[]{id}, mapper.rowMapper);
        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
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
