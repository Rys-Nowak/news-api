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

    private String makeRequest(String searchTerm, int page, int count) throws Exception {
        String subscriptionKey = API_KEY;
        String host = "https://api.bing.microsoft.com/";
        String path = "/v7.0/news/search";
        String searchQuery = searchTerm + "&count=" + count + "&offset=" + (page - 1) * count;
//        + "&mkt=en-us";
        URL url = new URL(host + path + "?q=" + searchQuery);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        InputStream stream = connection.getInputStream();
        String searchResult = new Scanner(stream).useDelimiter("\\A").next();

        return getSearchValue(searchResult);
    }

    private String getSearchValue(String json_text) {
        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject) parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        var value = json.getAsJsonArray("value");
        return gson.toJson(value);
    }

    private String concatJsonArrays(List<String> arrays) {
        JsonParser parser = new JsonParser();
        JsonArray allValues = new JsonArray();
        for (var array : arrays) {
            allValues.addAll((JsonArray) parser.parse(array));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(allValues);
    }

    public String searchObservedNews(int page, int count, String username) throws Exception {
        var allEntries = phraseRepository.findAll();
        var phrases = new ArrayList<String>();
        for (PhraseEntity phrase : allEntries) {
            if (phrase.username.equals(username))
                phrases.add(phrase.observedPhrase);
        }

        var searchResults = new ArrayList<String>(phrases.size());
        ;
        for (String phrase : phrases) {
            searchResults.add(makeRequest(phrase, page, count));
            Thread.sleep(340); // 3 requests per second - free access
        }

        return concatJsonArrays(searchResults);
    }

    public String searchNews(String searchTerm, int page, int count) throws Exception {
        return makeRequest(searchTerm, page, count);
    }
}
