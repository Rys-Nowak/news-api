package com.java.api.news.user;

import com.java.api.news.exception.UserNotFoundException;
import com.java.api.news.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService auth;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) throws UserNotFoundException {
        if (userRepository.findById(user.username).isEmpty())
            throw new UserNotFoundException();

        String sessionId = auth.verifyUser(user, userRepository.findById(user.username).get().password);

        ResponseCookie cookie = ResponseCookie.from("sessionId", sessionId)
                .httpOnly(true)
//                .secure(true)
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
        user.password = auth.hashPassword(user.password);
        userRepository.save(user);
        return user.username;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@CookieValue(name = "sessionId", defaultValue = "") String sessionId) {
        ResponseCookie deleteCookie = ResponseCookie
                .from("sessionId", "")
                .build();

        auth.clearSession(sessionId);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logged out");
    }
}
