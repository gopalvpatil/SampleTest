package com.westernalliancebancorp.positivepay.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.westernalliancebancorp.positivepay.dao.SsoAttributeDao;
import com.westernalliancebancorp.positivepay.dao.SsoDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Sso;
import com.westernalliancebancorp.positivepay.model.SsoAttribute;
import com.westernalliancebancorp.positivepay.service.BottomLineSecurityService;

/**
 * BottomLineSecurityServiceImpl is
 *
 * @author Giridhar Duggirala
 */
@Service(value = "bottomlineSecurityService")
public class BottomLineSecurityServiceImpl implements BottomLineSecurityService, InitializingBean {
    @Loggable
    private Logger logger;

    @Value("${positivepay.bottomline.csv.params.to.read}")
    private String csvParamsToRead;
    private String[] paramsToRead;
    @Autowired
    private SsoAttributeDao ssoAttributeDao;
    @Autowired
    private SsoDao ssoDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public String getUid(Map requestParameters) {
        Map<String, String> requestParams = new HashMap<String, String>(paramsToRead.length);
        for (String paramName : paramsToRead) {
            logger.debug(new StringBuilder().append("Reading param name :").append(paramName).append(" and the value is :").append(requestParameters.get(paramName)).toString());
            if (requestParameters.get(paramName) != null && ((String[]) requestParameters.get(paramName)).length > 0 &&
                    ((String[]) requestParameters.get(paramName))[0] != null && !((String[]) requestParameters.get(paramName))[0].isEmpty()) {
                requestParams.put(paramName, ((String[]) requestParameters.get(paramName))[0]);
            } else
                logger.error("Cannot consider the param as this is " + (String) requestParameters.get(paramName));
        }
        UUID randomUid = UUID.randomUUID();
        logger.debug("Generated random UID is :" + randomUid.toString());
        Sso sso = new Sso();
        sso.setUid(randomUid.toString());
        sso.setCreateTimeInMillis(System.currentTimeMillis());
        sso.setStatus(Sso.Status.CREATED);
        ssoDao.save(sso);
        logger.debug(new StringBuilder().append("Sso saved successfully with the id :").append(sso.getId()).append(" for UID ").append(sso.getUid()).toString());
        for (String key : requestParams.keySet()) {
            SsoAttribute ssoAttribute = new SsoAttribute();
            ssoAttribute.setName(key);
            ssoAttribute.setValue(requestParams.get(key));
            ssoAttribute.setSso(sso);
            ssoAttributeDao.save(ssoAttribute);
            logger.debug(new StringBuilder().append("SsoAttribute  saved successfully with the id :").append(ssoAttribute.getId()).append(" for sso UID").append(sso.getUid()).append(" name of the attribute ").append(ssoAttribute.getName()).append(" and the value : ").append(ssoAttribute.getValue()).toString());
        }
        return sso.getUid();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleLogout(String uid) {
        logger.info("BottomLine kill switch activated for the UID : "+uid);
        if (uid != null && !uid.isEmpty()) {
            Sso sso = ssoDao.findByUid(uid);
            sso.setStatus(Sso.Status.LOGGED_OUT);
            ssoDao.update(sso);
        }
        logger.info("BottomLine kill switch activation for the UID : "+uid+" is completed");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleBottomLineKill(String uid) {
        if (uid != null && !uid.isEmpty()) {
            Sso sso = ssoDao.findByUid(uid);
            sso.setStatus(Sso.Status.BOTTOM_LINE_KILL);
            ssoDao.update(sso);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(this.csvParamsToRead, "csvParamsToRead must be specified");
        paramsToRead = csvParamsToRead.split(",");
    }
}
