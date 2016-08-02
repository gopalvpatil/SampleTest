package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.group.Group;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * User: gduggirala
 * Date: 17/5/14
 * Time: 4:11 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class RoleServiceImplTest {
    @Autowired
    private RoleService roleService;

    @Test
    public void testGetPermissionByRoleId(){
        Group<Permission> permissionGroup = roleService.getRolePermissions(1l);
        String[] keys = permissionGroup.keySet().toArray(new String[0]);
        //All groups
        for(String key:keys){
            List<Permission> permissionList = permissionGroup.find(key);
            System.out.println("Group: "+ permissionList.get(0).getType().getDescription());
            for(Permission permission:permissionList){
                System.out.println(permission);
            }
        }
        //In case we know the group
        List<Permission> manualEntryList = permissionGroup.find(Permission.TYPE.MANUAL_ENTRY);
        System.out.println("Only manual entry");
        for(Permission permission:manualEntryList){
            System.out.println(permission);
        }
        System.out.print("Tester");
    }
}
