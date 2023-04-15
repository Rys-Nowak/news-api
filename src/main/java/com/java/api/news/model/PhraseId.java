package com.java.api.news.model;

import java.io.Serializable;

public class PhraseId implements Serializable {
    private String username;
    private String observedPhrase;

    public PhraseId(String username, String observedPhrase) {
        this.username = username;
        this.observedPhrase = observedPhrase;
    }
}
