package com.java.api.news.security;

import com.java.api.news.exception.InvalidPasswordException;
import com.java.api.news.exception.UserNotFoundException;
import com.java.api.news.user.UserDto;
import com.java.api.news.user.UserEntity;
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

    public UserDto authenticate(CredentialsDto credentials) {
        if (userRepository.findById(credentials.username()).isEmpty())
            throw new UserNotFoundException("User not found");

        String userPassword = userRepository.findById(credentials.username()).get().getPassword();
        if (encoder.matches(CharBuffer.wrap(credentials.password()), userPassword)) {
            return new UserDto(credentials.username(), createToken(credentials.username()));
        }
        throw new InvalidPasswordException();
    }

    public UserDto findByLogin(String username) {
        if (userRepository.findById(username).isPresent()) {
            return new UserDto(username, createToken(username));
        }
        throw new UserNotFoundException("User not found");
    }

    public String createToken(String username) {
        return username + "&" + calculateHmac(username);
    }

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

    public UserEntity createUser(String username, char[] password) {
        return userRepository.save(new UserEntity(username, hashPassword(String.valueOf(password))));
    }

    public String hashPassword(String password) {
        return encoder.encode(password);
    }
}
