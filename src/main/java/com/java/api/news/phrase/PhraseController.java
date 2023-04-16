package com.java.api.news.phrase;

import com.java.api.news.exception.PhraseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/phrase")
public class PhraseController {
    @Autowired
    private PhraseRepository phraseRepository;

    @GetMapping
    public Iterable<Phrase> getPhrases() {
        var allEntries = phraseRepository.findAll();
        var phrases = new ArrayList<Phrase>();
        for (Phrase phrase : allEntries) {
            if (phrase.username.equals("user"))
                phrases.add(phrase);
        }
        return phrases;
    }

    @PostMapping()
    public Phrase addPhrase(@RequestBody String phrase) {
        return phraseRepository.save(new Phrase("user", phrase));
    }

    @DeleteMapping()
    public void deletePhrase(@RequestBody String phrase) {
        var id = new PhraseId("user", phrase);
        if (phraseRepository.findById(id).equals(Optional.empty()))
            throw new PhraseNotFoundException();
        phraseRepository.deleteById(new PhraseId("user", phrase));
    }
}
