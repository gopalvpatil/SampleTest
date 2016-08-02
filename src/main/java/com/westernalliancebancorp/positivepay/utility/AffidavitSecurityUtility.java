package com.westernalliancebancorp.positivepay.utility;

import com.westernalliancebancorp.positivepay.log.Loggable;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * AffidavitSecurityUtility is used for encrypting the cookie.
 * Please refer to "SecretKeyGeneratorUtility" which is in test classes.
 *
 * @author Giridhar Duggirala
 */
@Service
public class AffidavitSecurityUtility {

    @Value("${positivepay.cookie.encryption.key}")
    private String key;

    @Value("${positivepay.encrypt.cookie}")
    private boolean encryptCookie;
    @Loggable
    private Logger logger;

    public String encrypt(String clearString, boolean urlSafe) throws UnsupportedEncodingException {
        return encryptIt(clearString, urlSafe);
    }

    public String encryptIt(String clearString, boolean urlSafe) throws UnsupportedEncodingException {
        if (!encryptCookie) {
            Base64 base64 = new Base64(76, "".getBytes(), urlSafe);
            return URLEncoder.encode(base64.encodeAsString(clearString.getBytes()), "UTF-8");
        }
        
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            final SecretKeySpec secretKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(key), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            Base64 base64 = new Base64(76, "".getBytes(), urlSafe);
            final String encryptedString = URLEncoder.encode(base64.encodeAsString(cipher.doFinal(clearString.getBytes())), "UTF-8");
            return encryptedString;
        } catch (Exception e) {
            logger.error("Exception whil encrypting the string", e);
        }
        
        return null;
    }

    public String decrypt(String encryptedString, boolean urlSafe) {
        return decryptIt(encryptedString, urlSafe);
    }

    public String decryptIt(String strToDecrypt, boolean urlSafe) {
        if (!encryptCookie) {
            Base64 base64 = new Base64(76, "".getBytes(), urlSafe);
            return new String(base64.decode(strToDecrypt));
        }
        
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            final SecretKeySpec secretKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(key), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            Base64 base64 = new Base64(76, "".getBytes(), urlSafe);
            final String decryptedString = new String(cipher.doFinal(base64.decode(strToDecrypt)));
            return decryptedString;
        } catch (Exception e) {
            System.err.println(e);
        }
        
        return null;
    }
}
