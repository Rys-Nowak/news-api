package com.java.api.news.user;

import com.java.api.news.exception.UserExistsException;
import com.java.api.news.security.AuthenticationService;
import com.java.api.news.security.CookieAuthenticationFilter;
import com.java.api.news.security.CredentialsDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService auth;

    /**
     * Enables login by username and password
     *
     * @param user            user data
     * @param servletResponse http response object
     * @return response with status 200 if successfully authenticated
     */
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(
            @AuthenticationPrincipal UserDto user,
            HttpServletResponse servletResponse
    ) {
        Cookie authCookie = new Cookie(CookieAuthenticationFilter.COOKIE_NAME, user.getToken());
        authCookie.setHttpOnly(true);
        authCookie.setSecure(true);
        authCookie.setMaxAge((int) Duration.of(1, ChronoUnit.DAYS).toSeconds());
        authCookie.setPath("/");

        servletResponse.addCookie(authCookie);

        return ResponseEntity.ok(user);
    }

    /**
     * Creates new user with given username and password
     *
     * @param user user data (request body)
     * @return New user's username
     * @throws UserExistsException if user with given username already exists in the database
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Validated CredentialsDto user)
            throws UserExistsException {
        if (userRepository.findById(user.username()).isPresent())
            throw new UserExistsException();

        UserEntity createdUser = userRepository.save(new UserEntity(user.username(), auth.hashPassword(String.valueOf(user.password()))));
        return ResponseEntity.ok().body(createdUser.getUsername());
    }

    /**
     * Logs user out - sets expired cookie
     *
     * @param request  http request object
     * @param response http response object
     * @return "Logged out" message if successfully logged out
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        Optional<Cookie> authCookie = Stream.of(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> CookieAuthenticationFilter.COOKIE_NAME.equals(cookie.getName()))
                .findFirst();

        authCookie.ifPresent(cookie -> {
            cookie.setMaxAge(0);
            response.addCookie(authCookie.get());
        });

        return ResponseEntity.ok("Logged out");
    }

    /**
     * Retrieves currently logged-in user data
     *
     * @param user user data
     * @return user data
     */
    @GetMapping("/user")
    public UserDto getUser(@AuthenticationPrincipal UserDto user) {
        return user;
    }
}
