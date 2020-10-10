package org.itranswarp.springioc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Validators {
    @Autowired
    private List<Validator> validatorList; //注入list 每新增一个Validator类型 自动被Spring装配到Validators中

    public void validate(String email, String password, String name){
        for(var validator : validatorList){
            validator.validate(email,password,name);
        }
    }
}
