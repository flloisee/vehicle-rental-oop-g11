package com.vehicle.rental.g11.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class PasswordUtil {

    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    // Recommended parameters: iterations, memory (KB), parallelism
    private static final int ITERATIONS = 3;
    private static final int MEMORY_KB = 65536; // 64 MB
    private static final int PARALLELISM = 1;

    public static String hashPassword(String plainPassword) {
        return argon2.hash(ITERATIONS, MEMORY_KB, PARALLELISM, plainPassword.toCharArray());
    }

    public static boolean verifyPassword(String hash, String plainPassword) {
        return argon2.verify(hash, plainPassword.toCharArray());
    }
}