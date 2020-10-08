package org.itranswarp.springioc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserService {
    @Autowired
    private MailService mailService;
    @Autowired
    private HikariDataSource dataSource;

    @Autowired
    private Validators validators;


    private List<User> users = new ArrayList<User>(List.of(
            new User(1,"bob@example.com", "password", "Bob"),
            new User(2, "ablic@example.com","password", "Alice"),
            new User(3,"tom@example.com","password","Tom")
    ));

    public User login(String email, String password){
        String sql = " select * from zj_user where Name=?; ";
        try {
            dataSource.query(sql, new Object[]{"xjlonly"});
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for(User user :users){
            if(user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)){
                mailService.sendLoginMail(user);
                return user;
            }
        }
        throw new RuntimeException("login failed");
    }

    public User getUser(long id) {
        return this.users.stream().filter(x->x.getId() == id).findFirst().orElseThrow();
    }

    public User register(String email, String password, String name){
        this.validators.validate(email,password,name);
        users.forEach(x->{
            if(x.getEmail().equalsIgnoreCase(email)){
                throw new RuntimeException("email exist");
            }
        });
        User user = new User(users.stream().mapToLong(x->x.getId()).max().getAsLong() + 1, email, password, name);
        users.add(user);
        mailService.sendRegistrationMail(user);

        return user;
    }


}
