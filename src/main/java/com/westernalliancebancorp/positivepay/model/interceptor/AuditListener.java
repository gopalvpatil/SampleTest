package com.westernalliancebancorp.positivepay.model.interceptor;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.threadlocal.AffidavitThreadLocal;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.web.security.Affidavit;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/21/13
 * Time: 4:41 PM
 */
@Component
public class AuditListener {
    Logger logger = org.slf4j.LoggerFactory.getLogger(AuditListener.class);
    @PrePersist
    public void prePersist(Object obj) {
        if (obj instanceof Auditable) {
            AuditInfo auditInfo = ((Auditable) obj).getAuditInfo();
            if (auditInfo == null) {
                auditInfo = new AuditInfo();
            }
            String name = SecurityUtility.getPrincipal();
            auditInfo.setCreatedBy(name);
            auditInfo.setModifiedBy(name);
            auditInfo.setDateCreated(new Date());
            auditInfo.setDateModified(new Date());
            Affidavit affidavit = AffidavitThreadLocal.get();
            if(affidavit != null) {
                if(affidavit.getType().equals(Affidavit.TYPE.EMULATED.toString())) {
                    auditInfo.setCreatedBy(affidavit.getCreatedByUserName());
                    auditInfo.setModifiedBy(affidavit.getCreatedByUserName());
                }
            }
        }
    }

    @PreUpdate
    public void preUpdate(Object obj) {
        if (obj instanceof Auditable) {
            AuditInfo auditInfo = ((Auditable) obj).getAuditInfo();
            if (auditInfo == null) {
                auditInfo = new AuditInfo();
            }
            String name = SecurityUtility.getPrincipal();
            auditInfo.setModifiedBy(name);
            auditInfo.setDateModified(new Date());
        }
    }
}
