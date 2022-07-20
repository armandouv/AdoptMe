package com.adoptme.pets;

public class Formatter {
    public static String getFormatted(String value) {
        if (value.isEmpty()) return value;
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
