package org.itranswarp.springioc.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)//注入list中 指定的顺序
public class EmailValidator implements Validator{
    @Override
    public void validate(String email, String password, String name) {
        if(!email.matches("^[a-z0-9]+\\@[a-z0-9]+\\.[a-z]{2,10}$")){
            throw new IllegalArgumentException("invail email:" + email);
        }
    }
}
