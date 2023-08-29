package com.mindhub.homebanking.functions;

import java.util.Random;

public class generateNumber {
    public static String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i=0;i < 4; i++) {
            int number = random.nextInt(9999);
            sb.append(String.format("%04d", number));
            if(i < 3) {
                sb.append("-");
            }
        }
        return sb.toString();
    }
}
