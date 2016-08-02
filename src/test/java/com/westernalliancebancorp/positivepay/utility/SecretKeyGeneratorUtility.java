package com.westernalliancebancorp.positivepay.utility;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * SecretKeyGeneratorUtility is
 *
 * @author Giridhar Duggirala
 */

public class SecretKeyGeneratorUtility {
    private static String key = null;
    private static String strToEncrypt = "This is a test string";
    public static void main(String a[]) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        key = DatatypeConverter.printHexBinary(secretKey.getEncoded());
        String encrypted = encrypt();
        System.out.println(key);
        System.out.println("Encrypted String = "+encrypted);
        System.out.println("Decrypted String = "+decrypt(encrypted));
    }

    public static String encrypt()
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            final SecretKeySpec secretKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(key), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            final String encryptedString = Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
            return encryptedString;
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
        return null;

    }

    public static String decrypt(String strToDecrypt)
    {
        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            final SecretKeySpec secretKey = new SecretKeySpec(DatatypeConverter.parseHexBinary(key), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            final String decryptedString = new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
            return decryptedString;
        }
        catch (Exception e)
        {
            System.err.println(e);

        }
        return null;
    }
}
