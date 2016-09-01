package com.baremind.algorithm;

import java.util.Random;

/**
 * Created by fixopen on 3/6/15.
 */
public class RandomGenerator {
    public byte[] generateRandom(final int length) {
        byte[] result = new byte[length];
        Random random = new Random();
        random.nextBytes(result);
        return result;
    }
}
