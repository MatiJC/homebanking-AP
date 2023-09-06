package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.repositories.AccountRepository;
import java.util.Random;

public class generateNumber {
    static Random random = new Random();
    static AccountRepository accountRepository;

    public static String generateCardNumber() {
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
