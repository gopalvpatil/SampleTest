package com.westernalliancebancorp.positivepay.aspect;

import com.westernalliancebancorp.positivepay.annotation.PositivePaySecurity;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.web.security.Affidavit;
import com.westernalliancebancorp.positivepay.web.security.UserPermission;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: gduggirala
 * Date: 29/4/14
 * Time: 11:47 AM
 */
@Component
@Aspect
public class PositivePaySecurityAspect {
    @Loggable
    private Logger logger;
    @Autowired
    UserDetailDao userDetailDao;

    @Around("@annotation(positivePaySecurity)")
    public Object buildExceutionSequence(ProceedingJoinPoint joinPoint, PositivePaySecurity positivePaySecurity)
            throws Throwable {
        String principalLoginName = SecurityUtility.getPrincipal();
        String resourceName = positivePaySecurity.resource();
        Permission.TYPE TYPE = positivePaySecurity.group();
        String errorMessage = positivePaySecurity.errorMessage();
        Affidavit affidavit = AffidavitThreadLocal.get();
        List<Permission> permissionList = new ArrayList<Permission>();
        boolean isPermitted = false;
        if (affidavit == null) {
            UserDetail userDetail = userDetailDao.findByUserName(principalLoginName);
            Set<Permission> permissionSet = userDetailDao.getPermissionsByUserDetailId(userDetail.getId());
            permissionList.addAll(permissionSet);
        }else{
            permissionList = affidavit.getPermissionList();
        }
        for (Permission permission : permissionList) {
            if ((permission.getName()).name().equalsIgnoreCase(resourceName)) {
                isPermitted = true;
                break;
            }
        }
        if (isPermitted) {
            return joinPoint.proceed();
        } else {
            throw new SecurityException("User " + principalLoginName + " " + errorMessage);
        }
    }
}
