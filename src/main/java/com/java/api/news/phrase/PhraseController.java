package com.java.api.news.phrase;

import com.java.api.news.exception.PhraseNotFoundException;
import com.java.api.news.security.AuthenticationService;
import com.java.api.news.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/phrase")
public class PhraseController {
    @Autowired
    private PhraseRepository phraseRepository;

    @Autowired
    private AuthenticationService auth;

    /**
     * Gets all observed by user phrases
     *
     * @param user user data
     * @return all user's observed phrases
     */
    @GetMapping
    public Iterable<String> getPhrases(@AuthenticationPrincipal UserDto user) {
        var allEntries = phraseRepository.findAll();
        var phrases = new ArrayList<String>();
        for (PhraseEntity phrase : allEntries) {
            if (phrase.username.equals(user.getUsername()))
                phrases.add(phrase.observedPhrase);
        }
        return phrases;
    }

    /**
     * Adds new phrase to user's observed phrases
     *
     * @param user   user data
     * @param phrase new phrase to observe (request body)
     * @return created PhraseEntity object - new observed phrase and username
     */
    @PostMapping()
    public PhraseEntity addPhrase(
            @AuthenticationPrincipal UserDto user,
            @RequestBody String phrase
    ) {
        return phraseRepository.save(new PhraseEntity(user.getUsername(), phrase));
    }

    /**
     * Deletes user's observed phrase
     *
     * @param user   user data
     * @param phrase phrase to delete (request body)
     * @throws PhraseNotFoundException if user does not observe given phrase
     */
    @DeleteMapping()
    public void deletePhrase(
            @AuthenticationPrincipal UserDto user,
            @RequestBody String phrase
    ) throws PhraseNotFoundException {
        var id = new PhraseId(user.getUsername(), phrase);
        if (phraseRepository.findById(id).isEmpty())
            throw new PhraseNotFoundException("Current user does not observe this phrase");

        phraseRepository.deleteById(new PhraseId(user.getUsername(), phrase));
    }
}
