package com.java.api.news.security;

import com.java.api.news.exception.WrongPasswordException;
import com.java.api.news.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class AuthenticationService {
    @Autowired
    private PasswordEncoder encoder;
    private final HashMap<String, String> sessions = new HashMap<>();

    public String verifyUser(User user, String encodedPassword) throws WrongPasswordException {
        if (!encoder.matches(user.password, encodedPassword))
            throw new WrongPasswordException();

        String sessionId = UUID.randomUUID().toString();
        sessions.put(user.username, sessionId);

        return sessionId;
    }

    public String verifySession(String sessionId) throws InvalidCookieException {
        if (!sessions.containsValue(sessionId))
            throw new InvalidCookieException("Invalid sessionId");

        return sessions.keySet().stream().filter((key) -> sessions.get(key).equals(sessionId)).toList().get(0);
    }

    public void clearSession(String sessionId) {
        sessions.remove(sessions.keySet().stream().filter((key) -> sessions.get(key).equals(sessionId)).toList().get(0));
    }

    public String hashPassword(String password) {
        return encoder.encode(password);
    }
}
