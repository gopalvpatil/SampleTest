package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author umeshram
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserPermissionDto implements Serializable{

	private static final long serialVersionUID = 8119346499164556618L;
	
	private String userName;
	private Long baseRoleId;
	private String newRoleName;
	private Map<Long, Boolean> permissions;//Key:Permission Id, value: true/false
	private List<UserPermissionDto.Role> roles = new ArrayList<UserPermissionDto.Role>();
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getBaseRoleId() {
		return baseRoleId;
	}
	
	public void setBaseRoleId(Long baseRoleId) {
		this.baseRoleId = baseRoleId;
	}
	
	public String getNewRoleName() {
		return newRoleName;
	}
	
	public void setNewRoleName(String newRoleName) {
		this.newRoleName = newRoleName;
	}
	
	public Map<Long, Boolean> getPermissions() {
		return permissions;
	}
	
	public void setPermissions(Map<Long, Boolean> permissions) {
		this.permissions = permissions;
	}
	
	public List<UserPermissionDto.Role> getRoles() {
		return roles;
	}

	public void setRoles(List<UserPermissionDto.Role> roles) {
		this.roles = roles;
	}

	public static class Role{
		private Long id;
		private String label;
		private List<Long> permissionIds = new ArrayList<Long>();
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public List<Long> getPermissionIds() {
			return permissionIds;
		}
		public void setPermissionIds(List<Long> permissionIds) {
			this.permissionIds = permissionIds;
		}
	}
}
