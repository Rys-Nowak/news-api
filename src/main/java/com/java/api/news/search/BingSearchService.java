package com.java.api.news.search;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

@Service
public class BingSearchService {
    public String searchNews(String searchQuery) throws Exception {
        return searchNews(searchQuery, 1);
    }

    public String searchNews(String searchTerm, int page) throws Exception {
        Dotenv dotenv = Dotenv.configure().load();
        String subscriptionKey = dotenv.get("BING_API_KEY");
        String host = dotenv.get("BING_API_ENDPOINT");
        String path = "/v7.0/news/search";
        String searchQuery = searchTerm + "&count=20&offset=" + (page - 1) * 20 + "&mkt=en-us";

        URL url = new URL(host + path + "?q=" + URLEncoder.encode(searchQuery, StandardCharsets.UTF_8));
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        InputStream stream = connection.getInputStream();
        return new Scanner(stream).useDelimiter("\\A").next();
    }
}
