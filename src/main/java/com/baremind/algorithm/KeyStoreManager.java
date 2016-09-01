package com.baremind.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * Created by fixopen on 3/6/15.
 */
public class KeyStoreManager {
    public int createKeyStore(final String storePath, final KeyPair keyPair, final Certificate[] certificates) {
        int result = 0;
        FileOutputStream fileOutputStream = null;
        try {
            KeyStore store = KeyStore.getInstance("JKS");
            store.load(null, "keyStorePassword".toCharArray());

            PrivateKey privateKey = keyPair.getPrivate();
            KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(privateKey, certificates);
            store.setEntry("privateKeyAlias", privateKeyEntry, new KeyStore.PasswordProtection("keyStorePassword".toCharArray()));

            File file = new File("keyStoreFilePath");
            fileOutputStream = new FileOutputStream(file);
            store.store(fileOutputStream, "keyStorePassword".toCharArray());
            fileOutputStream.close();
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public PrivateKey getSelfPrivateKey(final String storePath, final String alias, final String secret) {
        PrivateKey result = null;
        try {
            KeyStore store = KeyStore.getInstance("JKS");
            FileInputStream fileInputStream = new FileInputStream(storePath);
            store.load(fileInputStream, secret.toCharArray());

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) store
                .getEntry(alias, new KeyStore.PasswordProtection(secret.toCharArray()));

            fileInputStream.close();
            result = privateKeyEntry.getPrivateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
