package com.baremind.algorithm;

import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * Created by fixopen on 3/6/15.
 */
public class Signer {
    public byte[] sign(byte[] data, Certificate userCertificate) {
        byte[] result = null;
        return result;
    }

    public byte[] selfSign(byte[] data) {
        byte[] result = null;
        PrivateKey privateKey = Securities.keyStoreManager.getSelfPrivateKey("keyStoreFilePath", "keyAlias", "keySecret");
        result = Securities.lowLevelRSA.encryptByPrivateKey(data, privateKey);
        return result;
    }
}
