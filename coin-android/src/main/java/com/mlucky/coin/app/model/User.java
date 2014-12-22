package com.mlucky.coin.app.model;

/**
 * Created by m.iakymchuk on 17.12.2014.
 */
public class User {
    private String name;

    private String password;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }
}
