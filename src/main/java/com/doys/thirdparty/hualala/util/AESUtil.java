package com.doys.thirdparty.hualala.util;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
public class AESUtil {
    public AESUtil() {
    }

    public static String AESEncode(String encodeRules, String content) throws Exception {
        try {
            if (encodeRules != null && encodeRules.length() < 16) {
                encodeRules = encodeRules + encodeRules;
            }

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = content.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength += blockSize - plaintextLength % blockSize;
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            SecretKeySpec keyspec = new SecretKeySpec(encodeRules.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(encodeRules.getBytes());
            cipher.init(1, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            //return (new BASE64Encoder()).encode(encrypted);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception var10) {
            throw var10;
        }
    }
    public static String AESDncode(String encodeRules, String content) throws Exception {
        try {
            if (encodeRules != null && encodeRules.length() < 16) {
                encodeRules = encodeRules + encodeRules;
            }

            //byte[] encrypted1 = (new BASE64Decoder()).decodeBuffer(content);
            byte[] encrypted1 = Base64.getDecoder().decode(content);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(encodeRules.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(encodeRules.getBytes());
            cipher.init(2, keyspec, ivspec);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        } catch (Exception var8) {
            throw var8;
        }
    }
}