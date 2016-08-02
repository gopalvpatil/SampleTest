package com.westernalliancebancorp.positivepay.dao;

import java.util.Set;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.Role;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/25/13
 * Time: 2:41 PM
 */
public interface RoleDao extends GenericDao<Role, Long> {
    Role findByName(String roleName);
    Set<Permission> getRolePermissions(Long roleId);
    Role findRoleBy(Long roleId);
}
