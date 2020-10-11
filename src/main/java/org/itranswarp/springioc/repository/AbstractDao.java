package org.itranswarp.springioc.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

//抽象出dao层
public abstract class AbstractDao<T> extends JdbcDaoSupport {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String table;
    private Class<T> entityClass;
    private RowMapper<T> rowMapper;

    @PostConstruct
    public void  init(){
        super.setJdbcTemplate(jdbcTemplate);
    }


    public AbstractDao(){
        this.entityClass = getParameterizedType();
        this.table = this.entityClass.getSimpleName().toLowerCase()+ "s";
        this.rowMapper = new BeanPropertyRowMapper<>(entityClass);
    }
    public T getById(long id){
        return jdbcTemplate.queryForObject("Select * from " + table + " where id=? ", this.rowMapper, id);
    }

    public List<T> getAll(int pageIndex){
        int limit = 100;
        int offset = limit * (pageIndex -1);
        return getJdbcTemplate().query("select * from " + table + " limit ? offset ?", new Object[]{limit, offset},
                this.rowMapper);
    }

    public void deleteById(long id){
        getJdbcTemplate().update("delete from " + table + " where id= ?", id);
    }

    public RowMapper<T> getRowMapper(){
        return this.rowMapper;
    }

    private Class<T> getParameterizedType(){
        //获取带有泛型的父类类型
        Type type = getClass().getGenericSuperclass();
        if(!(type instanceof ParameterizedType)){
            throw new IllegalArgumentException("Class" + getClass().getName() + " does not have parameterized type");
        }
        //ParameterizedType参数化类型，即泛型
        ParameterizedType pt = (ParameterizedType)type;
        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        Type[] types = pt.getActualTypeArguments();
        if(types.length != 1){
            throw new IllegalArgumentException("Class" + getClass().getName() + " has more than 1 parameterized types");
        }
        Type r = types[0];
        if(!(r instanceof Class<?>)){
            throw new IllegalArgumentException("Class " + getClass().getName() + " does not have parameterized type " +
                    "of class");
        }
        return (Class<T>)r;
    }
}
