package org.itranswarp.springioc.myorm;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/*
* 存储entity相关属性 自动解析Bean 生成基础的SQL等
* */
public final class Mapper<T> {
    final Class<T> entityClass;
    final String tableName;

    //@Id property
    final AccessibleProperty id;

    //所有字段属性
    final List<AccessibleProperty> allProperties;

    //小写属性名称-> accessibleproperty
    final Map<String, AccessibleProperty> allPropertiesMap;

    final List<AccessibleProperty>   insertableProperties;
    final List<AccessibleProperty> updatableProperties;

    final Map<String, AccessibleProperty> updatablePropertiesMap;
    final RowMapper<T> rowMapper;

    final String selectSQL;
    final String insertSQL;
    final String updateSQL;
    final String deleteSQL;

    public Mapper(Class<T> clazz) throws Exception{
        //获取Javabean所有属性列表
        List<AccessibleProperty> all = getProperties(clazz);
        AccessibleProperty[] ids = all.stream().filter(AccessibleProperty::isId).toArray(AccessibleProperty[]::new);
        if(ids.length != 1){
            throw new RuntimeException("Require exact one @Id");
        }
        this.id = ids[0];
        this.allProperties = all;
        this.allPropertiesMap = buildPropertiesMap(this.allProperties);
        this.insertableProperties = all.stream().filter(AccessibleProperty::isInsertable).collect(Collectors.toList());
        this.updatableProperties = all.stream().filter(AccessibleProperty::isUpdatable).collect(Collectors.toList());
        this.updatablePropertiesMap = buildPropertiesMap(this.updatableProperties);
        this.entityClass = clazz;

        this.tableName = getTableName(clazz);
        this.selectSQL = buildSelectSql(this.tableName, this.id.columnName);
        this.insertSQL = buildInsertSql(this.tableName, this.insertableProperties);
        this.updateSQL = buildUpdateSql(this.tableName, this.updatableProperties, this.id.columnName);
        this.deleteSQL = buildDeleteSql(this.tableName, this.id.columnName);

        this.rowMapper = new BeanPropertyRowMapper<>(this.entityClass);
    }

    private String buildDeleteSql(String tableName, String columnName) {
        return "delete from " + tableName + " where " + columnName + " = ?";
    }

    private String buildUpdateSql(String tableName, List<AccessibleProperty> updatableProperties, String idName) {
        return " update " + tableName + " set "+
                String.join(",",
                        updatableProperties.stream().map(x->x.columnName + " = ?").toArray(String[]::new))
                + " where " + idName + " =? ";
    }

    private String buildInsertSql(String tableName, List<AccessibleProperty> insertableProperties) {
        return "insert into " + tableName + " ( "
                + String.join(",", insertableProperties.stream().map(x->x.columnName).toArray(String[]::new))
                + " ) values (" + numOfQuestions(insertableProperties.size()) + " )";
    }

    private String numOfQuestions(int size) {
        String[] qs = new String[size];
        return String.join(",", Arrays.stream(qs).map(x-> "?").toArray(String[]::new));
    }

    private String buildSelectSql(String tableName, String columnName) {
        return "select * from " + tableName + " where " + columnName + " = ? ";
    }

    private String getTableName(Class<T> clazz) {
        Table table= clazz.getAnnotation(Table.class);
        if(table != null && !table.name().isEmpty()){
            return table.name();
        }
        return clazz.getSimpleName();
    }


    private Map<String, AccessibleProperty> buildPropertiesMap(List<AccessibleProperty> properties) {
        Map<String,AccessibleProperty> map = new HashMap<>();
        for(var prop : properties){
            map.put(prop.propertyName.toLowerCase(), prop);
        }
        return map;
    }

    //获取属性列表
    private List<AccessibleProperty> getProperties(Class<?> clazz) throws  Exception{
        List<AccessibleProperty> properties = new ArrayList<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        for(var pd : beanInfo.getPropertyDescriptors()){
            //排除getClass()
            if(pd.getName().equals("class")){
                continue;
            }
            Method getter = pd.getReadMethod();
            Method setter = pd.getReadMethod();
            //忽略@Transient
            if(getter != null && getter.isAnnotationPresent(Transient.class)){
                continue;
            }
            if(getter != null && setter != null){
                properties.add(new AccessibleProperty(pd));
            }else {
                throw new RuntimeException("Property " + pd.getName() + " is not read/write");
            }
        }
        return properties;
    }
}
