package com.example.productmanager.firebase;

public class User {
    private UserType type;
    private String username;
    private String realname;
    private String email;
    private String pass;

    public User(String username, String realname, String email, String pass, UserType type) {
        this.type = type;
        this.username = username;
        this.realname = realname;
        this.email = email;
        this.pass = pass;
    }

    public User() { }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isEmpty() {
        return username == null
                && realname == null
                && email == null
                && pass == null;
    }
}
