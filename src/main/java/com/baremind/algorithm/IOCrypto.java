package com.baremind.algorithm;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fixopen on 3/6/15.
 */
public class IOCrypto {
    public OutputStream encrypt(InputStream inputStream) {
        OutputStream result = null;
        return result;
    }

    public OutputStream decrypt(InputStream inputStream) {
        OutputStream result = null;
        return result;
    }

//    static {
//        System.loadLibrary("crypto");
//    }

    private native int Decrypt(String src, String dst);

    private native int Encrypt(String src, String dst);
}
