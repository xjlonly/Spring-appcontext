package org.itranswarp.springioc.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.itranswarp.springioc.entity.User;

import java.util.List;

public interface UserManager {
    @Select("Select * from users where id=#{id}")
    User getById(@Param("id") long id);

    @Select("select * from users  limit #{offset},#{maxResults}")
    List<User> getAll(@Param("offset") int offset, @Param("maxResults") int maxResults);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("insert into users (email, password, name, createAt) Values (#{user.email}, #{user.password}, #{user" +
            ".name}, #{user.createAt})")
    void insert(@Param("user") User user);
}
