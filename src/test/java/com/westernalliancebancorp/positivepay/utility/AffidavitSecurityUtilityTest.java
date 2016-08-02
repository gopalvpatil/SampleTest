package com.westernalliancebancorp.positivepay.utility;

import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.web.security.Affidavit;
import com.westernalliancebancorp.positivepay.web.security.UserPermission;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * AffidavitSecurityUtilityTest is
 *
 * @author Giridhar Duggirala
 */

public class AffidavitSecurityUtilityTest {
    @Test
    public void testEncrypt() throws Exception {
        List<Permission> userPermissionList = new ArrayList<Permission>();
        Permission userPermission = new Permission();
        userPermission.setDescription("Role description");
       // userPermission.setName("ROLE_USER");
        userPermissionList.add(userPermission);
        Permission userPermission1 = new Permission();
        userPermission1.setDescription("Role description");
     //   userPermission1.setName("ROLE_ADMIN");
        userPermissionList.add(userPermission1);
        Affidavit affidavit =new Affidavit("usename",123123l, 123123123l, Affidavit.TYPE.NORMAL.toString(), userPermissionList);
        String toBase64 = GsonUtility.toString(affidavit);
        Base64 base64 = new Base64(76,"".getBytes(), true);
        String base64Encoded = base64.encodeAsString(toBase64.getBytes());
        Assert.isTrue(!base64Encoded.contains("\\r\\n"));
        Affidavit newAffidavit1 = GsonUtility.getAffidavit(new String(base64.decode(base64Encoded)));
        Assert.isTrue(newAffidavit1 != null);
    }
}
