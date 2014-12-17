package com.mlucky.coin.web.services;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

/**
 * Created by m.iakymchuk on 12.12.2014.
 */
public class ApplicationPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
