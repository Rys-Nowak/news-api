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
    private SearchService searchService;

    @GetMapping()
    private String getObservedNews(
            @CookieValue(name = "sessionId", defaultValue = "") String sessionId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "count", required = false) Integer count
    ) throws SearchApiConnectionException {
        if (page == null) page = 1;
        if (count == null) count = 10;
        String username = auth.verifySession(sessionId);

        String response;
        try {
            response = searchService.searchObservedNews(page, count, username);
        } catch (Exception e) {
            throw new SearchApiConnectionException(e.getMessage());
        }
        return response;
    }

    @GetMapping("/{phrase}")
    private String getNewsByPhrase(
            @CookieValue(name = "sessionId", defaultValue = "") String sessionId,
            @PathVariable String phrase,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "count", required = false) Integer count
    ) throws SearchApiConnectionException {
        if (page == null) page = 1;
        if (count == null) count = 20;
        auth.verifySession(sessionId);

        String response;
        try {
            response = searchService.searchNews(phrase, page, count);
        } catch (Exception e) {
            throw new SearchApiConnectionException(e.getMessage());
        }
        return response;
    }
}
