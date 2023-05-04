package com.java.api.news.search;


import com.java.api.news.exception.SearchApiConnectionException;
import com.java.api.news.security.AuthenticationService;
import com.java.api.news.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Autowired
    private AuthenticationService auth;
    @Autowired
    private SearchService searchService;

    /**
     * Gets news related to user's observed phrases
     *
     * @param user  user data
     * @param page  index of results' page
     * @param count amount of news related to one phrase
     * @return Json Array of news objects
     * @throws SearchApiConnectionException if it can not get proper response from bing search api
     */
    @GetMapping()
    private String getObservedNews(
            @AuthenticationPrincipal UserDto user,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "count", required = false) Integer count
    ) throws SearchApiConnectionException {
        if (page == null) page = 1;
        if (count == null) count = 15;

        String response;
        try {
            response = searchService.searchObservedNews(page, count, user.getUsername());
        } catch (Exception e) {
            throw new SearchApiConnectionException(e.getMessage());
        }
        return response;
    }

    /**
     * Gets news related to given phrase
     *
     * @param phrase phrase to search for (path variable)
     * @param page   index of results' page
     * @param count  amount of news related to the phrase
     * @return Json Array of news objects
     * @throws SearchApiConnectionException if it can not get proper response from bing search api
     */
    @GetMapping("/{phrase}")
    private String getNewsByPhrase(
            @PathVariable String phrase,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "count", required = false) Integer count
    ) throws SearchApiConnectionException {
        if (page == null) page = 1;
        if (count == null) count = 30;

        String response;
        try {
            response = searchService.searchNews(phrase, page, count);
        } catch (Exception e) {
            throw new SearchApiConnectionException(e.getMessage());
        }
        return response;
    }
}
