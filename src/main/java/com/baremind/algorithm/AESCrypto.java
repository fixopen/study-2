package com.baremind.algorithm;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Created by fixopen on 3/6/15.
 */
public class AESCrypto {
    public OutputStream encrypt(InputStream inputStream, byte[] key, byte[] algorithmParameter) {
        return cipher(inputStream, 1, key, algorithmParameter);
    }

    public OutputStream decrypt(InputStream inputStream, byte[] key, byte[] algorithmParameter) {
        return cipher(inputStream, 2, key, algorithmParameter);
    }

    private OutputStream cipher(InputStream inputStream, int mode, byte[] key, byte[] algorithmParameter) {
        OutputStream result = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                //
            }
        };
        try {
            SecretKeySpec k = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            if (algorithmParameter == null) {
                cipher.init(mode, k);
            } else {
                AlgorithmParameterSpec paramSpec = new IvParameterSpec(algorithmParameter);
                cipher.init(mode, k, paramSpec);
            }
            for (; ; ) {
                byte[] buffer = new byte[4096];
                int length = inputStream.read(buffer);
                byte[] cipherContent = cipher.update(buffer, 0, length);
                result.write(cipherContent);
                if (length < 4096) {
                    break;
                }
                cipherContent = cipher.doFinal(buffer);
                result.write(cipherContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return result;
    }

    public byte[] generateKey() {
        byte[] result = null;
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            SecretKey secretKey = generator.generateKey();
            result = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return result;
    }
}
