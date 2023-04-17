package com.java.api.news.search;


import com.java.api.news.exception.SearchApiConnectionException;
import com.java.api.news.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/search")
public class SearchController {
    @Autowired
    private AuthenticationService auth;
    @Autowired
    private BingSearchService searchService;

    @GetMapping("/{phrase}")
    private String getNewsByPhrase(
            @CookieValue(name = "sessionId", defaultValue = "") String sessionId,
            @PathVariable String phrase,
            @RequestParam(value = "page", required = false) Integer page
    ) throws SearchApiConnectionException {
        if (page == null) page = 1;
        auth.verifySession(sessionId);
        String response;
        try {
            response = searchService.searchNews(phrase, page);
        } catch (Exception e) {
            throw new SearchApiConnectionException(e.getMessage());
        }
        return response;
    }
}
