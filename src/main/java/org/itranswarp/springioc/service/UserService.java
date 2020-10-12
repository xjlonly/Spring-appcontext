package org.itranswarp.springioc.service;

import org.hibernate.FlushMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.itranswarp.springioc.aspect.MetricTime;
import org.itranswarp.springioc.entity.User;
import org.itranswarp.springioc.mybatis.UserManager;
import org.itranswarp.springioc.myorm.DbTemplate;
import org.itranswarp.springioc.repository.UserDao;
import org.itranswarp.springioc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserService {
    @Autowired
    private MailService mailService;
    @Autowired
    private HikariDataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Validators validators;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRepository userRepository;

    //注入Hibernate
    @Autowired
    @Qualifier("hibernatetemp")
    HibernateTemplate hibernateTemplate;

    //注入jpa 不在使用@Autowired 使用@PersistenceContext
    @PersistenceContext
    EntityManager em;

    //注入Mybatis mapper
    @Autowired
    UserManager userManager;

    @Autowired
    DbTemplate dbTemplate;


    private List<User> users = new ArrayList<User>(List.of(
            new User(1,"bob@example.com", "password", "Bob"),
            new User(2, "ablic@example.com","password", "Alice"),
            new User(3,"tom@example.com","password","Tom")
    ));

    public User login(String email, String password){
       /* String sql = " select * from zj_user where Name=?; ";
        try {
            dataSource.query(sql, new Object[]{"xjlonly"});
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }*/
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

    @MetricTime("register")//以注解方式实现AOP装配
    public User register(String email, String password, String name){
        this.validators.validate(email,password,name);
        users.forEach(x->{
            if(x.getEmail().equalsIgnoreCase(email)){
                throw new RuntimeException("email exist");
            }
        });
        //User user = new User(users.stream().mapToLong(x->x.getId()).max().getAsLong() + 1, email, password, name);
//        users.add(user);
        User user = userDao.insert(email,name,password,12344555);
        mailService.sendRegistrationMail(user);

        return user;
    }

    //
    @MetricTime("register")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Qualifier("hibernate")
    public User registerbyH(String email, String password, String name){
        User user= new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        hibernateTemplate.save(user);
        System.out.println(user.getId());
        return user;
    }
    /*
    * 使用example查询 Hibernate把该实例所以非null的属性拼成where条件
    * 使用findbyExample时 基本类型默认值不会为null 所以总是会加入where条件
    * */
    public User loginbyH(String email,String password){
        User example = new User();
        example.setEmail(email);
        example.setPassword(password);
        List<User> list = hibernateTemplate.findByExample(example);
        return  list.isEmpty() ? null : list.get(0);
    }

    public User loginbyHC(String email,String name, String password){
        DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
        criteria.add(
                Restrictions.and(
                    Restrictions.or(
                        Restrictions.eq("email",email),
                        Restrictions.eq("name",name)),
                Restrictions.eq("password",password))
        );
       var list = (List<User>)hibernateTemplate.findByCriteria(criteria);
        return  list.isEmpty() ? null : list.get(0);
    }

    public User getUserById(long id){
        User user = this.em.find(User.class, id);
        if(user == null){
            throw new RuntimeException("User not found by id : " + id);
        }
        return user;
    }

    public User getUserByIdMyBatis(long id){
        User user = userManager.getById(id);
        if(user == null){
            throw new RuntimeException("User not found by id");
        }
        return  user;
    }

    public  User getUserByMyORM(long id){
        User user = dbTemplate.get(User.class, id);
        if(user == null){
            throw new RuntimeException("User not found by id");
        }
        return user;
    }

    public  User insertUserORM(String email, String password, String name){
        User user= new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setCreateAt(System.currentTimeMillis());
        dbTemplate.insert(user);
        System.out.println(user.getId());
        return user;
    }

    //链式调用
    public String geNameEmail(String email){
        User user = dbTemplate.select("name").from(User.class).where("email=?", email).first();
        return user.getName();
    }

    public  List<User> getUsers(int pageIndex){
        int pageSize =100;
        return  dbTemplate.from(User.class).orderBy("id").limit((pageIndex -1) * pageSize, pageSize).list();
    }


}
