package org.itranswarp.springioc.myorm;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import java.lang.invoke.CallSite;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class Criteria<T> {
    DbTemplate db;
    Mapper<T> mapper;
    Class<T> clazz;
    List<String> select = null;
    boolean distinct = false;
    String table = null;
    String where = null;
    List<Object> whereParams = null;
    List<String> orderBy = null;
    int offset = 0;
    int maxResults = 0;

    Criteria(DbTemplate db){
        this.db = db;
    }

    String sql(){
        StringBuilder sb = new StringBuilder(128);
        sb.append("select ");
        sb.append(select == null ? "*": String.join(",",select));
        sb.append(" from ").append(mapper.tableName);
        if(where != null){
            sb.append(" where ").append(String.join(",", where));
        }
        if(orderBy != null){
            sb.append(" order by ").append(String.join(",", orderBy));
        }
        if(offset >=0 && maxResults > 0){
            sb.append(" limit ?, ? ");
        }
        String s = sb.toString();
        return s;
    }

    Object[] params(){
        List<Object> params = new ArrayList<>();
        if(where != null) {
            for (Object obj : whereParams){
                if(obj == null){
                    params.add(null);
                }
                else{
                    params.add(obj);
                }
            }
        }
        if(offset >=0 && maxResults > 0){
            params.add(offset);
            params.add(maxResults);
        }
        return params.toArray();
    }

    List<T> list(){
        return  db.jdbcTemplate.query(sql(),params(),mapper.rowMapper);
    }

    T first(){
        this.offset = 0;
        this.maxResults = 1;
        List<T> list = list();
        if(list.isEmpty()){
            return  null;
        }else{
            return list.get(0);
        }
    }
    T unique(){
        this.offset = 0;
        this.maxResults = 2;
        List<T> list = list();
        if (list.isEmpty()) {
            throw new NoResultException("Expected unique row but nothing found.");
        }
        if (list.size() > 1) {
            throw new NonUniqueResultException("Expected unique row but more than 1 rows found.");
        }
        return list.get(0);
    }
}
