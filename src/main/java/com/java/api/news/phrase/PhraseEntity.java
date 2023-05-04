package com.java.api.news.phrase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

@Entity
@IdClass(PhraseId.class)
public class PhraseEntity {
    @Id
    @Column(nullable = false)
    public String username;

    @Id
    @Column(nullable = false)
    public String observedPhrase;

    public PhraseEntity() {}

    public PhraseEntity(String username, String observedPhrase) {
        this.username = username;
        this.observedPhrase = observedPhrase;
    }
}
