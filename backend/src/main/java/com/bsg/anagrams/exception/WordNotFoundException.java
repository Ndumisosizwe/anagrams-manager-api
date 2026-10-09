package com.bsg.anagrams.exception;

public class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(String word) {
        super("Word not found: " + word);
    }
}
