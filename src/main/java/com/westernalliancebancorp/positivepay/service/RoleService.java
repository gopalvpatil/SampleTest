package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import ch.lambdaj.group.Group;

import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.Role;

/**
 * @author Gopal Patil
 *
 */
public interface RoleService {
	List<Role> findAll();
	void deleteRoleById(Long id) throws Exception;
    Group<Permission> getRolePermissions(Long roleId);
    Group<Permission> getAllPermissions();
	Long saveRole(String roleName, String roleLabel, List<Long> selectedPermissionIdsList);
	Role findRoleById(Long roleId);
	void updateRole(Long roleId, String roleName, String roleLabel, List<Long> selectedPermissionIdsList);
}
