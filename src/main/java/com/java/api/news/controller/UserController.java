package com.java.api.news.controller;

import com.java.api.news.model.User;
import com.java.api.news.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Cookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Cookie login(@RequestBody String username, @RequestBody String password) {
//        userRepository.findById(username)
//                .orElseThrow(UserNotFoundException::new);

        String passwordHash = password; // hash password
        // check password
        return new Cookie();
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PostMapping("/logout")

    public void logout() {
        // log out
    }
}
