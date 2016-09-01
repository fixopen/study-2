package com.baremind.utils;

import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by fixopen on 4/6/15.
 */
public class KeyPairConverter {
    private static final String KEY_ALGORITHM = "RSA";

    public static Key bytesToPublicKey(byte[] bytes) {
        Key result = null;
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            result = keyFactory.generatePublic(x509KeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] publicKeyToBytes(Key publicKey) {
        return publicKey.getEncoded();
    }

    public static Key bytesToPrivateKey(byte[] bytes) {
        Key result = null;
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(bytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            result = keyFactory.generatePrivate(pkcs8KeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] privateKeyToBytes(Key privateKey) {
        return privateKey.getEncoded();
    }

    public static Certificate bytesToCertificate(byte[] bytes) {
        Certificate result = null;
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            result = factory.generateCertificate(new ByteArrayInputStream(bytes));
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] certificateToBytes(Certificate certificate) {
        byte[] result = null;
        try {
            result = certificate.getEncoded();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
