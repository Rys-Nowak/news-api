package com.java.api.news.search;


import com.java.api.news.exception.SearchApiConnectionException;
import com.java.api.news.security.AuthenticationService;
import com.java.api.news.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal UserDto user,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "count", required = false) Integer count
    ) throws SearchApiConnectionException {
        if (page == null) page = 1;
        if (count == null) count = 10;

        String response;
        try {
            response = searchService.searchObservedNews(page, count, user.getUsername());
        } catch (Exception e) {
            throw new SearchApiConnectionException(e.getMessage());
        }
        return response;
    }

    @GetMapping("/{phrase}")
    private String getNewsByPhrase(
            @PathVariable String phrase,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "count", required = false) Integer count
    ) throws SearchApiConnectionException {
        if (page == null) page = 1;
        if (count == null) count = 20;

        String response;
        try {
            response = searchService.searchNews(phrase, page, count);
        } catch (Exception e) {
            throw new SearchApiConnectionException(e.getMessage());
        }
        return response;
    }
}
