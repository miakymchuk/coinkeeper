package com.mlucky.coin.web.model;


import javax.persistence.*;

/**
 * Created by m.iakymchuk on 08.12.2014.
 */
@Entity
@Table(name = "USER", uniqueConstraints = {@UniqueConstraint(columnNames =
        { "EMAIL", "NAME" }) })
public class User {

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private Integer id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "NAME")
    private String name;


    @Column(name = "PASSWORD")
    private String password;

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
