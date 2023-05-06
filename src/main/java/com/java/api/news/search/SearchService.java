package com.java.api.news.search;

import com.google.gson.*;
import com.java.api.news.phrase.PhraseEntity;
import com.java.api.news.phrase.PhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.*;
import java.util.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

@Service
public class SearchService {
    @Autowired
    private PhraseRepository phraseRepository;

    @Value("${bing.search.api.key}")
    private String API_KEY;

    private SearchResults makeRequest(String searchTerm, int page, int count) throws Exception {
        String subscriptionKey = API_KEY;
        String host = "https://api.bing.microsoft.com/";
        String path = "/v7.0/news/search";
        String searchQuery = searchTerm + "&count=" + count + "&offset=" + (page - 1) * count + "&mkt=en-us";
        URL url = new URL(host + path + "?q=" + searchQuery);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        InputStream stream = connection.getInputStream();
        String searchResult = new Scanner(stream).useDelimiter("\\A").next();

        return getSearchValue(searchResult);
    }

    private SearchResults getSearchValue(String json_text) {
        return new Gson().fromJson(json_text, SearchResults.class);
    }

    private SearchResults concatSearchResults(List<SearchResults> arrays) {
        SearchResults concatenatedResults = new SearchResults();
        concatenatedResults.value = new ArrayList<>();
        for (var resultSet : arrays) {
            concatenatedResults.value.addAll(resultSet.value);
        }

        return concatenatedResults;
    }

    /**
     * Makes request to Bing Search Api in order to get news
     * related to observed by user phrases, prepares data
     *
     * @param page     index of results' page
     * @param count    amount of news related to one phrase
     * @param username of the user
     * @return results of searching
     * @throws Exception if connection with api goes wrong
     */
    public SearchResults searchObservedNews(int page, int count, String username) throws Exception {
        var allEntries = phraseRepository.findAll();
        var phrases = new ArrayList<String>();
        for (PhraseEntity phrase : allEntries) {
            if (phrase.username.equals(username))
                phrases.add(phrase.observedPhrase);
        }

        var searchResults = new ArrayList<SearchResults>(phrases.size());
        for (String phrase : phrases) {
            searchResults.add(makeRequest(phrase, page, count));
            Thread.sleep(340); // 3 requests per second - free access
        }

        return concatSearchResults(searchResults);
    }

    /**
     * Makes request to Bing Search Api in order to get news
     * related to given phrase, prepares data
     *
     * @param searchTerm phrase to search
     * @param page       index of results' page
     * @param count      amount of news related to one phrase
     * @return results of searching
     * @throws Exception if connection with api goes wrong
     */
    public SearchResults searchNews(String searchTerm, int page, int count) throws Exception {
        return makeRequest(searchTerm, page, count);
    }
}
