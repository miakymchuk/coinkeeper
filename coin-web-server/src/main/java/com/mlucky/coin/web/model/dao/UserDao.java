package com.mlucky.coin.web.model.dao;

import com.mlucky.coin.web.model.User;

/**
 * Created by m.iakymchuk on 08.12.2014.
 */
public interface UserDao {

    public void addUser(User user);
    public void removeUser(Integer id);
}
