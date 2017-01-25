package com.kurtin.kurtin.helpers;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by cvar on 1/24/17.
 */

public final class SecureIdGenerator {
    private SecureRandom random = new SecureRandom();

    private static final SecureIdGenerator SECURE_ID_GENERATOR = new SecureIdGenerator();

    public String nextId() {
        return new BigInteger(130, random).toString(32);
    }

    public static SecureIdGenerator getGenerator(){
        return SECURE_ID_GENERATOR;
    }
}
