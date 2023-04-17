package com.java.api.news.exception;

public class PhraseNotFoundException extends RuntimeException {
    public PhraseNotFoundException(String message) {
        super(message);
    }
}