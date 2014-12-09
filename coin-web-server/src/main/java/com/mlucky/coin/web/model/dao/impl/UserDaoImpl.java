package com.mlucky.coin.web.model.dao.impl;

import com.mlucky.coin.web.model.User;
import com.mlucky.coin.web.model.dao.UserDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Created by m.iakymchuk on 08.12.2014.
 */

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    @Qualifier("sessionFactory")
    private SessionFactory sessionFactory;

    @Override
    public void addUser(User user) {
        sessionFactory.getCurrentSession().save(user);
    }

    @Override
    public void removeUser(Integer id) {
        User user = (User) sessionFactory.getCurrentSession().load(User.class, id);
        if (null != user) {
            sessionFactory.getCurrentSession().delete(user);
        }
    }

}
