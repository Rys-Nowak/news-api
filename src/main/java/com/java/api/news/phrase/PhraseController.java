package com.java.api.news.phrase;

import com.java.api.news.exception.PhraseNotFoundException;
import com.java.api.news.security.AuthenticationService;
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

    @Autowired
    private AuthenticationService auth;

    @GetMapping
    public Iterable<Phrase> getPhrases(
            @CookieValue(name = "sessionId", defaultValue = "") String sessionId
    ) {
        String username = auth.verifySession(sessionId);

        var allEntries = phraseRepository.findAll();
        var phrases = new ArrayList<Phrase>();
        for (Phrase phrase : allEntries) {
            if (phrase.username.equals(username))
                phrases.add(phrase);
        }
        return phrases;
    }

    @PostMapping()
    public Phrase addPhrase(
            @RequestBody String phrase, @CookieValue(name = "sessionId", defaultValue = ""
    ) String sessionId) {
        String username = auth.verifySession(sessionId);

        return phraseRepository.save(new Phrase(username, phrase));
    }

    @DeleteMapping()
    public void deletePhrase(
            @RequestBody String phrase, @CookieValue(name = "sessionId", defaultValue = "") String sessionId
    ) throws PhraseNotFoundException {
        String username = auth.verifySession(sessionId);

        var id = new PhraseId(username, phrase);
        if (phraseRepository.findById(id).isEmpty())
            throw new PhraseNotFoundException("Current user does not observe this phrase");

        phraseRepository.deleteById(new PhraseId(username, phrase));
    }
}
