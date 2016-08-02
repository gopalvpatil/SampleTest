package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.group.Group;
import com.westernalliancebancorp.positivepay.dto.CheckStatusDto;
import com.westernalliancebancorp.positivepay.dto.UserDto;
import com.westernalliancebancorp.positivepay.dto.UserPermissionDto;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;


/**
 * User: gduggirala
 * Date: 5/5/14
 * Time: 6:39 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class UserServiceImplTest {
    @Autowired
    UserService userService;

    @Test
    public void testGetUsersByCompanyId(){
        List<UserDto> userDtoList = userService.getUsersByCompanyId(1l);
        Assert.notNull(userDtoList);
    }

    @Test
    public void testGetUserPermission(){
        Group<Permission> permissionGroup = userService.getUserPermission("gduggira");
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

    @Test
    public void testEhcache() {
        long initialStartTime = System.currentTimeMillis();
        List<CheckStatusDto> checkStatusDtos = userService.getDisplayableCheckStatus();
        long secondTime = System.currentTimeMillis();
        System.out.println("Time 1 : "+(secondTime - initialStartTime));

        checkStatusDtos = userService.getDisplayableCheckStatus();
        long thirdTime = System.currentTimeMillis();
        System.out.println("Time 2 : "+(thirdTime - secondTime));
    }

    @Test
    public void testEhcacheUserRolesAndPermissions(){
        System.out.println("Started for IntraEdge 1 *********************************************************");
        long initialStartTime = System.currentTimeMillis();
        UserPermissionDto userPermissionDto = userService.getUserRoleAndPermissions("intraedge");
        long completeTime = System.currentTimeMillis();
        System.out.println("Time 1 intraedge : "+(completeTime - initialStartTime));
        System.out.println("Ended for IntraEdge 1 *********************************************************");

        System.out.println("Started for Jenos 1 *********************************************************");
        initialStartTime = System.currentTimeMillis();
        userPermissionDto = userService.getUserRoleAndPermissions("jenos");
        completeTime = System.currentTimeMillis();
        System.out.println("Time 1 jenos : "+(completeTime - initialStartTime));
        System.out.println("Ended for Jeno 1 *********************************************************");

        System.out.println("Started for IntraEdge 2 *********************************************************");
        initialStartTime = System.currentTimeMillis();
        userPermissionDto = userService.getUserRoleAndPermissions("intraedge");
        completeTime = System.currentTimeMillis();
        System.out.println("Time 2 intraedge : "+(completeTime - initialStartTime));
        System.out.println("Ended for IntraEdge 2 *********************************************************");

        System.out.println("Started for Jenos 2 *********************************************************");
        initialStartTime = System.currentTimeMillis();
        userPermissionDto = userService.getUserRoleAndPermissions("jenos");
        completeTime = System.currentTimeMillis();
        System.out.println("Time 2 jenos : "+(completeTime - initialStartTime));
        System.out.println("Ended for Jenos 2 *********************************************************");
    }

    @Test
    public void testEhcache2() {
        System.out.println("Started for IntraEdge 1 *********************************************************");
        long initialStartTime = System.currentTimeMillis();
        List<Permission> checkStatusDtos = userService.findResourcesByUser("intraedge");
        long completeTime = System.currentTimeMillis();
        System.out.println("Time 1 intraedge : "+(completeTime - initialStartTime));
        System.out.println("Ended for IntraEdge 1 *********************************************************");

        System.out.println("Started for Jenos 1 *********************************************************");
        initialStartTime = System.currentTimeMillis();
        checkStatusDtos = userService.findResourcesByUser("jenos");
        completeTime = System.currentTimeMillis();
        System.out.println("Time 1 jenos : "+(completeTime - initialStartTime));
        System.out.println("Ended for Jeno 1 *********************************************************");

        System.out.println("Started for IntraEdge 2 *********************************************************");
        initialStartTime = System.currentTimeMillis();
        checkStatusDtos = userService.findResourcesByUser("intraedge");
        completeTime = System.currentTimeMillis();
        System.out.println("Time 2 intraedge : "+(completeTime - initialStartTime));
        System.out.println("Ended for IntraEdge 2 *********************************************************");

        System.out.println("Started for Jenos 2 *********************************************************");
        initialStartTime = System.currentTimeMillis();
        checkStatusDtos = userService.findResourcesByUser("jenos");
        completeTime = System.currentTimeMillis();
        System.out.println("Time 2 jenos : "+(completeTime - initialStartTime));
        System.out.println("Ended for Jenos 2 *********************************************************");

    }
}
