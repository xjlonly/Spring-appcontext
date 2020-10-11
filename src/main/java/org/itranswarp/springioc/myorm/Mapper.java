package org.itranswarp.springioc.myorm;

/*
* 存储entity相关属性 自动解析Bean 生成基础的SQL等
* */
public final class Mapper<T> {
    final Class<T> entityClass;
    final  String tableName;

    final AccessibleProperty id;
    public Mapper(Class<T> clazz) throws Exception{
        entityClass = clazz;
    }
}
