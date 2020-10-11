package org.itranswarp.springioc.entity;

import org.hibernate.annotations.OptimisticLock;
import org.itranswarp.springioc.repository.UserRepository;

import javax.persistence.*;

@Entity //Hibernate所需映射注解
@Table(name = "users")
public class User extends AbstractEntity{
    private String Email;
    private String Password;
    private String Name;
    //使用Hibernate时，不要使用基本类型的属性，总是使用包装类型 如Long或Integer
    public User(){

    }
    public User(long id, String email, String password, String name) {
        setId(id);
        setPassword(password);
        setEmail(email);
        setName(name);
    }

    @Column(nullable = false,length = 100)
    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Column(nullable = false,length = 100)
    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    @Column(nullable = false,length = 100)
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
