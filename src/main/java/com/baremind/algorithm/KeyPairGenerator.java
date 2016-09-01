package com.baremind.algorithm;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
//import java.security.KeyPairGenerator;

/**
 * Created by fixopen on 3/6/15.
 */
public class KeyPairGenerator {
    public KeyPair generate() {
        KeyPair result = null;
        try {
            java.security.KeyPairGenerator gen = java.security.KeyPairGenerator.getInstance("RSA");
            gen.initialize(2048);
            result = gen.generateKeyPair();
            //result.getPrivate();
            //result.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
