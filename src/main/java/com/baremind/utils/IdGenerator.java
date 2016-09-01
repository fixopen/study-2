package com.baremind.utils;

import java.util.Date;

/**
 * Created by fixopen on 17/8/15.
 */
public class IdGenerator {
    private static short instanceId = 0;
    private static int previousTimestamp = 0;
    private static short serialNo = 0;

    public static long getNewId() {
        long result = (long) instanceId << 48;
        Date now = new Date();
        int timestamp = (int) (now.getTime() / 1000);
        if (timestamp == previousTimestamp) {
            serialNo++;
        } else {
            previousTimestamp = timestamp;
            serialNo = 0;
        }
        result |= ((long) timestamp << 16) & 0x0000FFFFFFFF0000l;
        result |= serialNo;
        return result;
    }
}
