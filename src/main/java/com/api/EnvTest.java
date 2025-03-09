package com.api;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvTest {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        System.out.println("DB_URL: " + dotenv.get("DB_URL"));
        System.out.println("DB_USERNAME: " + dotenv.get("DB_USERNAME"));
        System.out.println("DB_PASSWORD: " + dotenv.get("DB_PASSWORD"));
    }
}
