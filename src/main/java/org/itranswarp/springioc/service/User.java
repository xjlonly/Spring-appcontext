package org.itranswarp.springioc.service;

public class User {
    private long Id;
    private String Email;
    private String Password;
    private String Name;

    public User(long id, String email, String password, String name) {
        Id = id;
        Email = email;
        Password = password;
        Name = name;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
