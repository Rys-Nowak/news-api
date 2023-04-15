package com.java.api.news.controller;

import com.java.api.news.model.Phrase;
import com.java.api.news.model.PhraseId;
import com.java.api.news.repository.PhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/phrase")
public class PhraseController {
    @Autowired
    private PhraseRepository phraseRepository;

    @GetMapping
    public Iterable<Phrase> getPhrases() {
        var allEntries = phraseRepository.findAll();
        var phrases = new ArrayList<Phrase>();
        for (Phrase phrase : allEntries) {
            if (phrase.username == currentUser.username)
                phrases.add(phrase);
        }
        return phrases;
    }

    @PostMapping()
    public Phrase addPhrase(@RequestBody Phrase phrase) { // only phrase?
        return phraseRepository.save(phrase);
    }

    @DeleteMapping()
    public void deletePhrase(@RequestBody String phrase) {
        var id = new PhraseId(currentUser.username, phrase);
//        phraseRepository.findById(id).orElseThrow(PhraseNotFoundException::new);
        phraseRepository.deleteById(id);
    }
}
