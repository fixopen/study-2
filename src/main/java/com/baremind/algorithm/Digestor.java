package com.baremind.algorithm;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fixopen on 3/6/15.
 */
public class Digestor {

    private final String algorithm = "SHA-256";
    private int SegmentSize = 4096;

    public byte[] digest(String data) {
        byte[] result = null;
        try {
            MessageDigest d = MessageDigest.getInstance(algorithm);
            d.update(data.getBytes());
            result = d.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public byte[] digest(InputStream inputStream) {
        byte[] result = null;
        try {
            MessageDigest d = MessageDigest.getInstance(algorithm);
            for (; ; ) {
                byte[] buffer = new byte[SegmentSize];
                int length = inputStream.read(buffer);
                if (length == 0) {
                    break;
                }
                d.update(buffer, 0, length);
                if (length < SegmentSize) {
                    break;
                }
            }
            result = d.digest();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
