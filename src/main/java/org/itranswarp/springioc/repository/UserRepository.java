package org.itranswarp.springioc.repository;

import org.itranswarp.springioc.service.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

@Component
public class UserRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public User getUserById(int id){
        String sql = " select * from users where id=? ";
        return jdbcTemplate.execute((Connection connection) -> {
            try(var ps = connection.prepareStatement(sql)) {
                ps.setObject(1, id);
                try(var rs = ps.executeQuery()){
                    if (rs.next()){
                        return  new User(
                                rs.getLong("id"),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getString("name")
                        );
                    }
                    throw new RuntimeException("not found user by id");
                }
            }
        });
    }
    public User getUserName(String name){
        String sql = " select * from  users where name=? ";
        return jdbcTemplate.execute(sql, (PreparedStatement preparedStatement) -> {
            preparedStatement.setObject(1, name);
            try(var rs = preparedStatement.executeQuery()){
                if (rs.next()){
                    return  new User(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("name")
                    );
                }
                throw new RuntimeException("not found user by id");
            }
        });
    }
}
