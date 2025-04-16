package com.example.urlshortener.service;

import com.example.urlshortener.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    };

    public User registerUser(String username, String password) {
        // Check if username already exists
        if (findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(password);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO app_user (username, password, created_at) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, username);
            ps.setString(2, encodedPassword);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        User user = new User();
        user.setId(keyHolder.getKey().longValue());
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM app_user WHERE username = ?",
                rowMapper,
                username
        );

        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(users.get(0));
    }
    // Add this method to the existing UserService class

    public boolean validateUserCredentials(String username, String password) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return passwordEncoder.matches(password, user.getPassword());
    }

    public Optional<User> findById(Long id) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM app_user WHERE id = ?",
                rowMapper,
                id
        );

        if (users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(users.get(0));
    }
}