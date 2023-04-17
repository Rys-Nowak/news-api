package com.java.api.news.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class User {
    @Id
    @Column(nullable = false, unique = true)
    public String username;

    @Column(nullable = false)
    public String password;
}
