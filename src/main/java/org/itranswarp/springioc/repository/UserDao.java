package org.itranswarp.springioc.repository;

import org.itranswarp.springioc.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

@Component
@Transactional
public class UserDao extends AbstractDao<User>{
    public User getUserById(long id){
        return getById(id);
    }
    public User getUserName(String name){
        String sql = " select * from  users where name=? ";
        assert getJdbcTemplate() != null;
        return getJdbcTemplate().execute(sql, (PreparedStatement preparedStatement) -> {
            preparedStatement.setObject(1, name);
            //preparedStatement实例由JdbcTemplate创建 并在回调后自动释放
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

    public User getUserEmail(String email){
        String sql = " select * from users where email= ?";
        assert getJdbcTemplate() != null;
        return getJdbcTemplate().queryForObject(sql, new Object[]{email},
                (ResultSet rs, int rowNum)->{
                    //将ResultSet的当前行映射为一个JavaBean
                    return new User(
                            rs.getLong("id"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("name")
                    );
                });
    }


    public void updateUser(User user){
        assert getJdbcTemplate() != null;
        if(1 != getJdbcTemplate().update("update users set  name=? where id=?", user.getName(),user.getId())){
            throw new RuntimeException("User not found by id");
        }
    }

    public User insert(String email, String name, String password,long createat){
        //创建一个KeyHolder 简化接收自增列的值
        KeyHolder holder = new GeneratedKeyHolder();
        assert getJdbcTemplate() != null;
        if(1 != getJdbcTemplate().update(
                connection -> {
                    var ps = connection.prepareStatement(
                            "Insert into users(email,name,password,createat) values (?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setObject(1,email);
                    ps.setObject(2,name);
                    ps.setObject(3,password);
                    ps.setObject(4,createat);
                    return ps;
                }, holder)) {
            throw new RuntimeException(" insert failed");
        }
        return new User(Objects.requireNonNull(holder.getKey()).longValue(),email,password,name);
    }


}
