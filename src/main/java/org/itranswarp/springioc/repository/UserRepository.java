package org.itranswarp.springioc.repository;

import org.itranswarp.springioc.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

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
        return jdbcTemplate.queryForObject(sql, new Object[]{email},
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

    public List<User> getUsers(int pageIndex){
        int limit = 100;
        int offset = limit * (pageIndex -1);
        return jdbcTemplate.query("select * from users limit ? offset ?", new Object[]{limit,offset},
                new BeanPropertyRowMapper<>(User.class)
                );
    }

    public void updateUser(User user){
        if(1 != jdbcTemplate.update("update users set  name=? where id=?", user.getName(),user.getId())){
            throw new RuntimeException("User not found by id");
        }
    }

    /*
    * 声明式事务 在发生RuntimeException时将自动回滚
    * 可在注解中指定针对特定异常的回滚
    * */
    @Transactional(rollbackFor = {RuntimeException.class, IOException.class}) //标识需要事物支持 开启事物支持 具体实现仍然由AOP代理
    public User insert(String email, String name, String password,long createat){
        //创建一个KeyHolder 简化接收自增列的值
        KeyHolder holder = new GeneratedKeyHolder();
        if(1 != jdbcTemplate.update(
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

    /*
    * 绝大部分情况下使用REQUIRED级别 SUPPORTS和REQUIRED_NEW 少数情况下会用到 其他基本不会用到
    * Spring传播事务是依靠ThreadLocal 因此事务能正确传播的前提是方法调用是在一个线程中进行
    * */
    @Transactional(propagation =  Propagation.REQUIRED) //事务传播级别绝
    public long getUserNum(){
        return  jdbcTemplate.queryForObject("select count(*) from users",
                (ResultSet rs, int rowNum)-> rs.getLong(1));
    }
}
