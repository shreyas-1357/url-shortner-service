package com.example.urlshortener.service;

import com.example.urlshortener.model.ShortenedUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private static final int SHORT_CODE_LENGTH = 6;
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final SecureRandom random = new SecureRandom();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<ShortenedUrl> rowMapper = (rs, rowNum) -> {
        ShortenedUrl url = new ShortenedUrl();
        url.setId(rs.getLong("id"));
        url.setOriginalUrl(rs.getString("original_url"));
        url.setShortCode(rs.getString("short_code"));
        url.setUserId(rs.getLong("user_id"));
        url.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        url.setAccessCount(rs.getLong("access_count"));
        return url;
    };

    public ShortenedUrl shortenUrl(String originalUrl, Long userId) {
        // Generate a random short code
        String shortCode = generateUniqueShortCode();
        return saveUrl(originalUrl, shortCode, userId);
    }

    public ShortenedUrl shortenUrlWithCustomCode(String originalUrl, String customCode, Long userId) {
        // Check if custom code already exists
        if (isShortCodeExists(customCode)) {
            throw new IllegalArgumentException("Custom short code already in use");
        }
        return saveUrl(originalUrl, customCode, userId);
    }

    private ShortenedUrl saveUrl(String originalUrl, String shortCode, Long userId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO shortened_url (original_url, short_code, user_id, created_at) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, originalUrl);
            ps.setString(2, shortCode);
            ps.setObject(3, userId);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);

        ShortenedUrl url = new ShortenedUrl();
        url.setId(keyHolder.getKey().longValue());
        url.setOriginalUrl(originalUrl);
        url.setShortCode(shortCode);
        url.setUserId(userId);
        url.setCreatedAt(LocalDateTime.now());
        url.setAccessCount(0L);
        return url;
    }

    public Optional<ShortenedUrl> findByShortCode(String shortCode) {
        List<ShortenedUrl> urls = jdbcTemplate.query(
                "SELECT * FROM shortened_url WHERE short_code = ?",
                rowMapper,
                shortCode
        );

        if (urls.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(urls.get(0));
    }

    public void incrementAccessCount(String shortCode) {
        jdbcTemplate.update(
                "UPDATE shortened_url SET access_count = access_count + 1 WHERE short_code = ?",
                shortCode
        );
    }

    public List<ShortenedUrl> findUrlsByUserId(Long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM shortened_url WHERE user_id = ? ORDER BY created_at DESC",
                rowMapper,
                userId
        );
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomShortCode();
        } while (isShortCodeExists(shortCode));
        return shortCode;
    }

    private String generateRandomShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(BASE62_CHARS.length());
            sb.append(BASE62_CHARS.charAt(randomIndex));
        }
        return sb.toString();
    }

    private boolean isShortCodeExists(String shortCode) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM shortened_url WHERE short_code = ?",
                Integer.class,
                shortCode
        );
        return count != null && count > 0;
    }
}