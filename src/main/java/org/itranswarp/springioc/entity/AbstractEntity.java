package org.itranswarp.springioc.entity;

import com.sun.istack.Nullable;

import javax.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@MappedSuperclass//标注用于继承
public abstract class AbstractEntity {
    private Long id;
    private Long createAt;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,updatable = false)
    public Long getId() {
        return id;
    }

    @Column(updatable = false)
    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    //标注一个虚拟属性，而不是从数据库里读取值 Hibernate不会从数据库里读取CreatedDateTime自带
    @Transient
    public ZonedDateTime getCreatedDateTime(){
        return Instant.ofEpochMilli(this.createAt).atZone(ZoneId.systemDefault());
    }

    @PrePersist //标识在JAVABean持久化到数据库之前 Hibernate会先执行该方法
    public void preInsert(){
        setCreateAt(System.currentTimeMillis());
    }
    public void setId(Long id) {
        this.id = id;
    }

}
