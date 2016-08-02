package com.westernalliancebancorp.positivepay.web.security;

import com.westernalliancebancorp.positivepay.model.Permission;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 2/5/14
 * Time: 2:25 PM
 */
public class UserPermission {
    public Long id;
    public String name;
    public Permission.TYPE TYPE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
