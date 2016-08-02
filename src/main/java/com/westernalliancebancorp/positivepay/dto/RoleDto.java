package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;

/**
 * @author Gopal Patil
 *
 */
public class RoleDto implements Serializable {

	private static final long serialVersionUID = -7855199522208959050L;
	
	private Long roleId;

	private String roleName;

	private String roleDescription;
	
	private String roleLabel;
	
	private Long[] selectedIds;
	
	private String edit;

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public String getRoleLabel() {
		return roleLabel;
	}

	public void setRoleLabel(String roleLabel) {
		this.roleLabel = roleLabel;
	}

	public Long[] getSelectedIds() {
		return selectedIds;
	}

	public void setSelectedIds(Long[] selectedIds) {
		this.selectedIds = selectedIds;
	}

	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}	

}
