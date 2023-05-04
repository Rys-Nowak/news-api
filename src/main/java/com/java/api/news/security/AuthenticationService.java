package com.java.api.news.security;

import com.java.api.news.exception.InvalidPasswordException;
import com.java.api.news.exception.UserNotFoundException;
import com.java.api.news.user.UserDto;
import com.java.api.news.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

@Service
public class AuthenticationService {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Value("${auth.key}")
    private String secretKey;

    /**
     * Authenticates user by username and password
     *
     * @param credentials username and password
     * @return user data with authentication token (no password)
     * @throws InvalidPasswordException if passwords do not match
     */
    public UserDto authenticate(CredentialsDto credentials) throws InvalidPasswordException {
        if (userRepository.findById(credentials.username()).isEmpty())
            throw new UserNotFoundException("User not found");

        String userPassword = userRepository.findById(credentials.username()).get().getPassword();
        if (encoder.matches(CharBuffer.wrap(credentials.password()), userPassword)) {
            return new UserDto(credentials.username(), createToken(credentials.username()));
        }
        throw new InvalidPasswordException();
    }

    /**
     * Finds user by their username
     *
     * @param username
     * @return user data
     */
    public UserDto findByLogin(String username) throws UserNotFoundException {
        if (userRepository.findById(username).isPresent()) {
            return new UserDto(username, createToken(username));
        }
        throw new UserNotFoundException("User not found");
    }

    /**
     * Finds user by authentication token
     *
     * @param token logged-in user's authentication token
     * @return user data
     */
    public UserDto findByToken(String token) {
        String[] parts = token.split("&");

        String login = parts[0];
        String hmac = parts[1];

        UserDto userDto = findByLogin(login);

        if (!hmac.equals(calculateHmac(userDto.getUsername())) || !Objects.equals(login, userDto.getUsername())) {
            throw new RuntimeException("Invalid Cookie value");
        }

        return userDto;
    }

    /**
     * Creates encoded authentication token for user based on their name and secret key
     *
     * @param username
     * @return authentication token
     */
    public String createToken(String username) {
        return username + "&" + calculateHmac(username);
    }

    private String calculateHmac(String username) {
        byte[] secretKeyBytes = Objects.requireNonNull(secretKey).getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = Objects.requireNonNull(username).getBytes(StandardCharsets.UTF_8);

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(valueBytes);
            return Base64.getEncoder().encodeToString(hmacBytes);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Encodes password
     *
     * @param password plain text to encode
     * @return encoded password
     */
    public String hashPassword(String password) {
        return encoder.encode(password);
    }
}
