package com.westernalliancebancorp.positivepay.web.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.lambdaj.group.Group;

import com.westernalliancebancorp.positivepay.dto.RoleDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.Role;
import com.westernalliancebancorp.positivepay.service.RoleService;
import com.westernalliancebancorp.positivepay.service.model.GenericResponse;

/**
 * @author Gopal Patil
 *
 */
@Controller
public class RoleController {
	
    @Loggable
    private Logger logger;
    
    @Autowired
    RoleService roleService;
    
	/**
	 * This method is invoked when the Admin wants to view all roles in the system
	 * 
	 * @param request
	 * @param model
	 * @return next page
	 * @throws Exception
	 */
	@RequestMapping(value = "/role/userroles")
	public String viewUserRoles(Model model, HttpServletRequest request)
			throws Exception {
		List<Role> roleList = roleService.findAll();
		model.addAttribute("roleList", roleList);
		return "site.view.role.page";
	}
	
	@RequestMapping(value = "/roles" , method = RequestMethod.GET)
	@ResponseBody
	public Map<Long, String> getAllRoles() throws Exception {
		List<Role> roleList = roleService.findAll();
		Map<Long, String> map = new HashMap<Long, String>();
		for(Role role : roleList)
			map.put(role.getId(), role.getName());
		return map;
	}
	
	@RequestMapping(value = "/role/allpemissions", method = RequestMethod.GET)
	public @ResponseBody
	Map<String, List<Permission>> getAllRolePermissions() throws Exception {
		Map<String, List<Permission>> groupMap = new HashMap<String, List<Permission>>();
		Group<Permission> permissionGroup = null;		
		try {
			permissionGroup = roleService.getAllPermissions();
			
	        //In case we know the group
	        List<Permission> itemsList = permissionGroup.find(Permission.TYPE.ITEMS);
	        List<Permission> manualEntryList = permissionGroup.find(Permission.TYPE.MANUAL_ENTRY);
	        List<Permission> userRoleManagementList = permissionGroup.find(Permission.TYPE.USER_ROLE_MANAGEMENT);
	        List<Permission> otherPermissionsList = permissionGroup.find(Permission.TYPE.OTHER_PERMISSIONS);
	        List<Permission> paymentsList = permissionGroup.find(Permission.TYPE.PAYMENTS);
	        
	        groupMap.put("itemsList", itemsList);
	        groupMap.put("manualEntryList", manualEntryList);
	        groupMap.put("userRoleManagementList", userRoleManagementList);
	        groupMap.put("otherPermissionsList", otherPermissionsList);
	        groupMap.put("paymentsList", paymentsList);
	        
		} catch (Exception ex) {
			throw new Exception("Failed in fetching all permissions.");
		}
		return groupMap;
	}
	
	/**
	 * @param roleDto
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/role/saverole", method = RequestMethod.POST)
	public @ResponseBody
	String saveRole(@RequestBody RoleDto roleDto, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String returnText;		
		if(roleDto.getEdit().equals("true")) {			
			try {
				if (roleDto.getRoleId() != null && roleDto.getSelectedIds() != null) {
					Long[] selectedIdsArray = roleDto.getSelectedIds();
					List<Long> selectedIdsList = Arrays.asList(selectedIdsArray);
					roleService.updateRole(roleDto.getRoleId(), roleDto.getRoleName(), roleDto.getRoleLabel(), selectedIdsList);
					returnText = "Role is edited successfully.";
				} else {
					returnText = "No role is selected to edit";
					logger.info("No role is selected to edit");
				}
			} catch (Exception e) {
				logger.error(
						"Exception is thrown by: Role Controller And Exception is: "
								+ e.getMessage(), e);
				returnText = "Problem Occured, Role is not edited";
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}			
		} else {			
			try {
				if (roleDto.getSelectedIds() != null) {
					Long[] selectedIdsArray = roleDto.getSelectedIds();
					List<Long> selectedIdsList = Arrays.asList(selectedIdsArray);	
					roleService.saveRole(roleDto.getRoleName(), roleDto.getRoleLabel(), selectedIdsList);
					returnText = "Role is saved successfully.";
				} else {
					returnText = "No role is selected to save";
					logger.info("No permission Id is selected to save");
				}
			} catch (Exception e) {
				logger.error(
						"Exception is thrown by: Role Controller And Exception is: "
								+ e.getMessage(), e);
				e.printStackTrace();
				returnText = "Problem Occured, Role is not saved.";
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		return returnText;
	}
	
	/**
	 * This method is invoked when the Admin wants to delete role
	 * 
	 * @return next page
	 * @throws Exception
	 */
	@RequestMapping(value = "/role/deleterole")
	public @ResponseBody
	String deleteRoleById(@RequestBody Role role, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String returnText = null;
		try {
			if (role.getId() != null) {
				roleService.deleteRoleById(role.getId());
				returnText = "Role is deleted successfully.";
			} else {
				returnText = "No role is selected to delete";
				logger.info("No role is selected to delete");
			}
		} catch(DataIntegrityViolationException div) {
			logger.error(
					"Exception is thrown by: Role Controller And Exception is: "
							+ div.getMessage(), div);
			returnText = "Role is assigned to user and can not be deleted";
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			
		} catch (Exception e) {
			logger.error(
					"Exception is thrown by: Role Controller And Exception is: "
							+ e.getMessage(), e);
			returnText = "Problem Occured, Role is not deleted";
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return returnText;
	}
	
	@RequestMapping(value = "/role/rolepermissions", method = RequestMethod.POST)
	public @ResponseBody
	Role getRoleAndPermissions(@RequestBody Role role1, HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		Role role = null;
		try {			
			if (role1.getId() != null) {
				role = roleService.findRoleById(role1.getId());
			}
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			throw new Exception("Failed in fetching role.");			
		}
		return role;
	}	
   
	@ExceptionHandler(Exception.class)
	public @ResponseBody
	GenericResponse handleException(HttpServletRequest request,
			HttpServletResponse response, Exception ex) {
		GenericResponse genericResponse = new GenericResponse("Failed: "
				+ ex.getMessage());
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return genericResponse;
	}

}
