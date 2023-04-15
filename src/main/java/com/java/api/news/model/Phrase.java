package com.java.api.news.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

@Entity
@IdClass(PhraseId.class)
public class Phrase {
    @Id
    @Column(nullable = false)
    public String username;

    @Id
    @Column(nullable = false)
    private String observedPhrase;
}
