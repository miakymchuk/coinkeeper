package com.mlucky.coin.web.services;

import com.mlucky.coin.web.model.User;

/**
 * Created by m.iakymchuk on 08.12.2014.
 */
public interface UserService {
    public void addUser(User user);
    public void removeUser(Integer Id);
}
