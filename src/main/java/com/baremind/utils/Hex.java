package com.baremind.utils;

/**
 * Created by fixopen on 3/6/15.
 */
public class Hex {
    public static String bytesToHex(byte[] bytes) {
        StringBuilder buffer = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if (((int) bytes[i] & 0xff) < 0x10) { //小于十前面补零
                buffer.append("0");
            }
            buffer.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buffer.toString();
    }

    public static byte[] hexToBytes(String hex) {
        byte[] result = null;
        if (hex.length() > 1) {
            result = new byte[hex.length() / 2];
            for (int i = 0; i < hex.length() / 2; i++) {
                int high = Integer.parseInt(hex.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hex.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }
        }
        return result;
    }
}
