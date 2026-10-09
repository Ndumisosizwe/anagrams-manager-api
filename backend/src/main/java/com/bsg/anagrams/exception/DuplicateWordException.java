package com.bsg.anagrams.exception;

public class DuplicateWordException extends RuntimeException {
    public DuplicateWordException(String word) {
        super("Word already exists: " + word);
    }
}
