package org.semicorp.msc.userapi.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * Encrypt String data using key generate with generateKey() method.
     * KeyGen class
     * @param data - String to encrypt
     * @param base64Key - AES key encrypted as base64
     * @return -  base64 encrypted string
     * @throws Exception
     */
    public static String encrypt(String data, String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(data.getBytes(CHARSET_NAME));
        byte[] encryptedIVAndText = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedIVAndText, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(encryptedIVAndText);
    }

    public static String decrypt(String encryptedData, String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        byte[] encryptedIvTextBytes = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[16];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
        int encryptedSize = encryptedIvTextBytes.length - iv.length;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, iv.length, encryptedBytes, 0, encryptedSize);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] original = cipher.doFinal(encryptedBytes);
        return new String(original, CHARSET_NAME);
    }

    /**
     * Used to generate encryption key and return in as base64 string.
     * This method is used only when it is needed to generate a new key.
     * Note: Keys are stored as env vars for security reasons
     * @return String encrypted key
     * @throws Exception
     */
    public static String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // for AES-256
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}