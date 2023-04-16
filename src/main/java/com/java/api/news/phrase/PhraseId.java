package com.java.api.news.phrase;

import java.io.Serializable;

public class PhraseId implements Serializable {
    private String username;
    private String observedPhrase;

    public PhraseId() {}

    public PhraseId(String username, String observedPhrase) {
        this.username = username;
        this.observedPhrase = observedPhrase;
    }
}
