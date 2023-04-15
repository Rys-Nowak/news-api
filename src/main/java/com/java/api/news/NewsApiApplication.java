package com.java.api.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.java.api.news.repository")
@EntityScan("com.java.api.news.model")
@SpringBootApplication
public class NewsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsApiApplication.class, args);
    }
}
