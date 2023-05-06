package com.java.api.news.search;

import java.util.List;

public class SearchResults {
    public List<SearchResult> value;
}

class SearchResult {
    public String name;
    public String url;
    public Img image;
    public String description;
    public List<Provider> provider;
    public String datePublished;
    public String category;
}

class Img {
    public Thumbnail thumbnail;
}

class Thumbnail {
    public String contentUrl;
}

class Provider {
    public String _type;
    public String name;
    public Img image;
}
