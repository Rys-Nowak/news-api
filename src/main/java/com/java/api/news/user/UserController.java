package com.java.api.news.user;

import com.java.api.news.exception.UserNotFoundException;
import com.java.api.news.security.AuthenticationService;
import com.java.api.news.security.CookieAuthenticationFilter;
import com.java.api.news.security.CredentialsDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService auth;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(
            @AuthenticationPrincipal UserDto user,
            HttpServletResponse servletResponse
    ) throws UserNotFoundException {
        Cookie authCookie = new Cookie(CookieAuthenticationFilter.COOKIE_NAME, user.getToken());
        authCookie.setHttpOnly(true);
        authCookie.setSecure(true);
        authCookie.setMaxAge((int) Duration.of(1, ChronoUnit.DAYS).toSeconds());
        authCookie.setPath("/");

        servletResponse.addCookie(authCookie);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Validated CredentialsDto user) {
        UserEntity createdUser = auth.createUser(user.username(), user.password());
        return ResponseEntity.ok().body(createdUser.getUsername());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public UserDto getUser(@AuthenticationPrincipal UserDto user) {
        return user;
    }
}
