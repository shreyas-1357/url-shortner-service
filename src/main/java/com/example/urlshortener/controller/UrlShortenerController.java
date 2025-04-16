package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlShortenRequestDto;
import com.example.urlshortener.dto.UrlShortenResponseDto;
import com.example.urlshortener.model.ShortenedUrl;
import com.example.urlshortener.model.User;
import com.example.urlshortener.service.UrlShortenerService;
import com.example.urlshortener.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/url")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private UserService userService;

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody UrlShortenRequestDto request, Authentication authentication) {
        try {
            ShortenedUrl shortenedUrl;
            Long userId = null;

            // If user is authenticated, get their ID
            if (authentication != null && authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
                if (userOpt.isPresent()) {
                    userId = userOpt.get().getId();
                }
            }

            // Check if custom code is requested (only for authenticated users)
            if (request.getCustomCode() != null && !request.getCustomCode().isEmpty()) {
                if (userId == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Map.of("error", "You must be logged in to use custom short codes"));
                }
                shortenedUrl = urlShortenerService.shortenUrlWithCustomCode(
                        request.getOriginalUrl(), request.getCustomCode(), userId);
            } else {
                shortenedUrl = urlShortenerService.shortenUrl(request.getOriginalUrl(), userId);
            }

            String shortUrl = buildShortUrl(shortenedUrl.getShortCode());

            UrlShortenResponseDto response = new UrlShortenResponseDto(
                    shortenedUrl.getOriginalUrl(),
                    shortUrl,
                    shortenedUrl.getShortCode()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/redirect/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) {
        Optional<ShortenedUrl> urlOpt = urlShortenerService.findByShortCode(shortCode);

        if (urlOpt.isPresent()) {
            ShortenedUrl url = urlOpt.get();
            urlShortenerService.incrementAccessCount(shortCode);
            return new RedirectView(url.getOriginalUrl());
        } else {
            RedirectView redirectView = new RedirectView("/not-found");
            redirectView.setStatusCode(HttpStatus.NOT_FOUND);
            return redirectView;
        }
    }

    @GetMapping("/my-urls")
    public ResponseEntity<?> getMyUrls(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "You must be logged in to view your URLs"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<ShortenedUrl> urls = urlShortenerService.findUrlsByUserId(user.getId());

            List<Map<String, Object>> responseUrls = urls.stream()
                    .map(url -> {
                        Map<String, Object> urlMap = new HashMap<>();
                        urlMap.put("originalUrl", url.getOriginalUrl());
                        urlMap.put("shortUrl", buildShortUrl(url.getShortCode()));
                        urlMap.put("shortCode", url.getShortCode());
                        urlMap.put("createdAt", url.getCreatedAt());
                        urlMap.put("accessCount", url.getAccessCount());
                        return urlMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of("urls", responseUrls));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not found"));
        }
    }

    private String buildShortUrl(String shortCode) {
        // This should be configured based on your application's base URL
        return "http://localhost:8080/api/url/redirect/" + shortCode;
    }
}
