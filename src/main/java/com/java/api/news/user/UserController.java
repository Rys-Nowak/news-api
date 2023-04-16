package com.java.api.news.user;

import com.java.api.news.exception.UserNotFoundException;
import com.java.api.news.exception.WrongPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        if (userRepository.findById(user.username).isEmpty())
            throw new UserNotFoundException();

        if (!encoder.matches(user.password, userRepository.findById(user.username).get().password))
            throw new WrongPasswordException();

        ResponseCookie cookie = ResponseCookie.from("username", user.username)
                .httpOnly(true)
                .path("/api")
                .maxAge(60 * 60 * 24) // 24h
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(user.username);
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        user.password = encoder.encode(user.password);
        userRepository.save(user);
        return user.username;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        ResponseCookie deleteCookie = ResponseCookie
                .from("user", "")
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logged out");
    }
}
