package com.java.api.news.repository;

import com.java.api.news.model.Phrase;
import com.java.api.news.model.PhraseId;
import org.springframework.data.repository.CrudRepository;

public interface PhraseRepository extends CrudRepository<Phrase, PhraseId> {
}
