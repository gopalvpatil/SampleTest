package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.lambdaj.group.Group;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.PermissionDao;
import com.westernalliancebancorp.positivepay.model.Permission;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.RoleDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Role;
import com.westernalliancebancorp.positivepay.service.RoleService;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.group;
import static ch.lambdaj.Lambda.on;

/**
 * @author Gopal Patil
 *
 */
@Service
public class RoleServiceImpl implements RoleService {
	
	/** The logger object */
	@Loggable
	private Logger logger;
	
	@Autowired
	RoleDao roleDao;

    @Autowired
    PermissionDao permissionDao;
    
    @Autowired
    private BatchDao batchDao;

	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.service.RoleService#findAll()
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public List<Role> findAll() {		
		return roleDao.findAll();
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void deleteRoleById(Long id) throws Exception{		
		Role role = roleDao.findById(id);	
		if (role != null) {
			roleDao.delete(role);
		} else{
			throw new Exception("Role not found for id:"+id);
		}
		
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Group<Permission> getRolePermissions(Long roleId) {
        Set<Permission> permissionSet = roleDao.getRolePermissions(roleId);
        Group<Permission> permissionGroup = group(permissionSet, by(on(Permission.class).getType()));
        logger.debug(""+permissionGroup);
        return permissionGroup;
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Group<Permission> getAllPermissions() {
        List<Permission> permissionList = permissionDao.findAll();
        Group<Permission> permissionGroup = group(permissionList, by(on(Permission.class).getType()));
        logger.debug(""+permissionGroup);
        return permissionGroup;
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public Long saveRole(String roleName, String roleLabel, List<Long> selectedPermissionIdsList) {
		Role role = new Role();
		role.setName(roleName);
		role.setDescription(roleName);
		role.setLabel(roleLabel);
		roleDao.save(role);
		if(selectedPermissionIdsList != null && !selectedPermissionIdsList.isEmpty())
			batchDao.insertRolePermissions(role.getId(), selectedPermissionIdsList);	
		return role.getId();
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Role findRoleById(Long roleId) {
        Role role = roleDao.findRoleBy(roleId);
        return role;
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void updateRole(Long roleId, String roleName, String roleLabel, List<Long> selectedPermissionIdsList) {		
		Role role = roleDao.findRoleBy(roleId);
		role.setName(roleName);
		role.setLabel(roleLabel);
		roleDao.update(role);	
		
		// Delete all permissions already assigned to role
		if(role.getPermissions() != null && !role.getPermissions().isEmpty()) {
			Set<Permission> permissionsAssignedSet = role.getPermissions();
			List<Long> assignedPermissionIdsList = new ArrayList<Long>();
	        for (Permission permission : permissionsAssignedSet) {
	        	assignedPermissionIdsList.add(permission.getId());
	        }			
			batchDao.deleteRolePermissions(roleId, assignedPermissionIdsList);	
		}
		
		// Insert selected permissions to role	
		batchDao.insertRolePermissions(roleId, selectedPermissionIdsList);		
	}
}
