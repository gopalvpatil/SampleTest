/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Permission;

public interface PermissionDao extends GenericDao<Permission, Long> {
    List<Permission> findResourcesByUserAndResourceName(String userName, String resourceName);
    List<Permission> findResourcesByUser(String userName);
    List<Permission> findByRoleId(Long roleId);
    List<Permission> findResourcesByUserAndType(String userName, Permission.TYPE type);
}
