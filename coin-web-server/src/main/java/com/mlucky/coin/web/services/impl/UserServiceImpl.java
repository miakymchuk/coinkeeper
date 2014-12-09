package com.mlucky.coin.web.services.impl;

import com.mlucky.coin.web.model.User;
import com.mlucky.coin.web.model.dao.UserDao;
import com.mlucky.coin.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by m.iakymchuk on 08.12.2014.
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Transactional
    public void addUser(User user) {
        userDao.addUser(user);
    }

    @Transactional
    public void removeUser(Integer Id) {
        userDao.removeUser(Id);
    }
}
