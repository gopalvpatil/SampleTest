package com.westernalliancebancorp.positivepay.model.interceptor;

import com.westernalliancebancorp.positivepay.model.AuditInfo;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/21/13
 * Time: 4:13 PM
 */
public interface Auditable {
    AuditInfo getAuditInfo();
    void setAuditInfo(AuditInfo auditInfo);
}
