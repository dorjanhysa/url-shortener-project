package com.dorjan.urlshortener.util;

import java.util.UUID;

public class ShortCodeGenerator {

    private ShortCodeGenerator() {}

    public static String generate() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
