package com.nhnacademy.aiot.modbus.server;

import java.io.IOException;
import java.util.Random;

public class test {
    public static void main(String[] args) throws IOException {

        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            double randomValue = 20 + Math.random() * 10;
            System.out.println((int) (randomValue * 100));
        }
        // for (int i = 0; i < 10; i++) {
        // int randomNumber = random.nextInt(10) + 100;
        // System.out.println(randomNumber);
        // }

    }
}
