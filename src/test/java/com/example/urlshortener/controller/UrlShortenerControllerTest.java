//package com.example.urlshortener.controller;
//
//import com.example.urlshortener.dto.UrlShortenRequestDto;
//import com.example.urlshortener.model.ShortenedUrl;
//import com.example.urlshortener.service.UrlShortenerService;
//import com.example.urlshortener.service.UserService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
////import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(UrlShortenerController.class)
//public class UrlShortenerControllerTest {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@MockBean
//	private UrlShortenerService urlShortenerService;
//
//	@MockBean
//	private UserService userService;
//
//	private final ObjectMapper objectMapper = new ObjectMapper();
//
//	// Test case for shortening URL for an anonymous user
//	@Test
//	public void testShortenUrl_AnonymousUser() throws Exception {
//		UrlShortenRequestDto request = new UrlShortenRequestDto();
//		request.setOriginalUrl("https://example.com");
//
//		ShortenedUrl shortenedUrl = new ShortenedUrl();
//		shortenedUrl.setOriginalUrl("https://example.com");
//		shortenedUrl.setShortCode("abc123");
//
//		Mockito.when(urlShortenerService.shortenUrl(eq("https://example.com"), eq(null)))
//				.thenReturn(shortenedUrl);
//
//		mockMvc.perform(post("/api/url/shorten")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(objectMapper.writeValueAsString(request)))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.originalUrl").value("https://example.com"))
//				.andExpect(jsonPath("$.shortCode").value("abc123"));
//	}
//
//	// Test case for redirecting to the original URL (successful case)
//	@Test
//	public void testRedirectToOriginalUrl_Found() throws Exception {
//		ShortenedUrl url = new ShortenedUrl();
//		url.setOriginalUrl("https://example.com");
//		url.setShortCode("abc123");
//
//		Mockito.when(urlShortenerService.findByShortCode("abc123"))
//				.thenReturn(Optional.of(url));
//
//		mockMvc.perform(get("/api/url/redirect/abc123"))
//				.andExpect(status().is3xxRedirection())
//				.andExpect(redirectedUrl("https://example.com"));
//	}
//
//	// Test case for redirecting to the original URL (not found)
//	@Test
//	public void testRedirectToOriginalUrl_NotFound() throws Exception {
//		Mockito.when(urlShortenerService.findByShortCode("notfound"))
//				.thenReturn(Optional.empty());
//
//		mockMvc.perform(get("/api/url/redirect/notfound"))
//				.andExpect(status().isNotFound());
//	}
//
//	// Optional: Test case for an authenticated user shortening URL
//	@Test
//	@WithMockUser(username = "testUser", roles = {"USER"})
//	public void testShortenUrl_AuthenticatedUser() throws Exception {
//		UrlShortenRequestDto request = new UrlShortenRequestDto();
//		request.setOriginalUrl("https://example.com");
//
//		ShortenedUrl shortenedUrl = new ShortenedUrl();
//		shortenedUrl.setOriginalUrl("https://example.com");
//		shortenedUrl.setShortCode("abc123");
//
//		Mockito.when(urlShortenerService.shortenUrl(eq("https://example.com"), eq("testUser")))
//				.thenReturn(shortenedUrl);
//
//		mockMvc.perform(post("/api/url/shorten")
//						.contentType(MediaType.APPLICATION_JSON)
//						.content(objectMapper.writeValueAsString(request)))
//				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.originalUrl").value("https://example.com"))
//				.andExpect(jsonPath("$.shortCode").value("abc123"));
//	}
//}
