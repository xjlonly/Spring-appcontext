package org.itranswarp.springioc.myorm;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.stream.FactoryConfigurationError;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

final class AccessibleProperty {
    final Method getter;
    final  Method setter;

    //属性类型
    final Class<?> propertyType;

    //属性名称
    final  String propertyName;

    //表字段名称
    final String columnName;

    boolean isId(){
        //判断方法上是否有Id注解
        return this.getter.isAnnotationPresent(Id.class);
    }
    //判断是否是Id且标注了注解@GeneratedValue(strategy=GenerationType.IDENTITY)
    boolean isIdentityId(){
        if(!isId()){
            return false;
        }
        GeneratedValue gv = this.getter.getAnnotation(GeneratedValue.class);
        if(gv == null){
            return  false;
        }
        var gt = gv.strategy();
        return gt == GenerationType.IDENTITY;
    }

    boolean isInsertable(){
        if(isIdentityId()){
            return false;
        }
        Column column = this.getter.getAnnotation(Column.class);
        return column == null || column.insertable();
    }

    boolean isUpdatable(){
        if(isIdentityId()){
            return false;
        }
        Column column = this.getter.getAnnotation(Column.class);
        return column == null || column.updatable();
    }

    public AccessibleProperty(PropertyDescriptor propertyDescriptor){
        this.getter = propertyDescriptor.getReadMethod();
        this.setter = propertyDescriptor.getWriteMethod();
        this.propertyName = propertyDescriptor.getName();
        this.propertyType = propertyDescriptor.getReadMethod().getReturnType();
        this.columnName = getColumnName(propertyDescriptor.getReadMethod(),propertyName);
    }

    public static  String getColumnName(Method method, String defaultName){
        var column = method.getAnnotation(Column.class);
        if(column == null || column.name().isEmpty()){
            return defaultName;
        }
        return column.name();
    }
}
