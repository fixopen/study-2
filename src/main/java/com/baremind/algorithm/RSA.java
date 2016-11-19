package com.baremind.algorithm;

import javax.crypto.Cipher;
import java.security.Key;

/**
 * Created by fixopen on 3/6/15.
 */
class RSA {
    private static final String KEY_ALGORITHM = "RSA";

    public byte[] encryptByPublicKey(byte[] data, Key publicKey) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            result = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return result;
    }

    public byte[] decryptByPrivateKey(byte[] cipherText, Key privateKey) {
        return decryptByKey(cipherText, privateKey);
    }

    byte[] encryptByPrivateKey(byte[] data, Key privateKey) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            result = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    byte[] decryptByPublicKey(byte[] data, Key publicKey) {
        return decryptByKey(data, publicKey);
    }

    private byte[] decryptByKey(byte[] cipherText, Key key) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            result = cipher.doFinal(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
