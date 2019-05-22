package org.afecam.convention.services;

import java.util.Random;

public class PassCodeGenerator {

    public static String generate(int length) {
        String passCode = "";
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            passCode += (char) (random.nextInt(26) + 'a');
        }

        return passCode.toUpperCase();
    }
}
