package org.itranswarp.springioc.entity;

import javax.persistence.*;

@Entity
@Table(name = "books")
public class Book extends AbstractEntity{
    private String name;

    public Book(String name) {
        setName(name);
    }

    public Book() {
    }


    @Column(nullable = false,length = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
