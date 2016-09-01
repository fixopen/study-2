package com.baremind.algorithm;

import com.baremind.data.Device;
import com.baremind.data.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Created by fixopen on 3/6/15.
 */
public class CertificateManager {
    public Certificate getCertificate(User user, Device device) {
        Certificate result = null;
        //find by local-cache
        return result;
    }

    public Certificate getSelfCertificate(final String certPath) {
        X509Certificate result = null;
        FileInputStream fileInputStream = null;
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            fileInputStream = new FileInputStream(new File(certPath));
            result = (X509Certificate) factory.generateCertificate(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public boolean verifySign(byte[] sign, byte[] plain, Certificate certificate) {
        boolean result = false;
        byte[] decryptContent = Securities.lowLevelRSA.decryptByPublicKey(sign, certificate.getPublicKey());
        if (Arrays.equals(decryptContent, plain)) {
            result = true;
        }
        return result;
    }
}
