//package com.example.urlshortener.controller;
//
//import com.example.urlshortener.dto.LoginRequestDto;
//import com.example.urlshortener.dto.UserRegistrationDto;
//import com.example.urlshortener.model.User;
//import com.example.urlshortener.service.UserService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(AuthController.class)
//public class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private AuthenticationManager authenticationManager;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Test
//    public void testRegisterUser_Success() throws Exception {
//        UserRegistrationDto dto = new UserRegistrationDto();
//        dto.setUsername("shreyas123");
//        dto.setPassword("testpass");
//
//        User mockUser = new User();
//        mockUser.setUsername(dto.getUsername());
//
//        Mockito.when(userService.registerUser(dto.getUsername(), dto.getPassword()))
//                .thenReturn(mockUser);
//
//        mockMvc.perform(post("/api/auth/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("User registered successfully"))
//                .andExpect(jsonPath("$.username").value("shreyas123"));
//    }
//
//    @Test
//    public void testLogin_Success() throws Exception {
//        LoginRequestDto loginRequest = new LoginRequestDto();
//        loginRequest.setUsername("shreyas123");
//        loginRequest.setPassword("testpass");
//
//        Authentication auth = Mockito.mock(Authentication.class);
//
//        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(auth);
//
//        mockMvc.perform(post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Login successful"))
//                .andExpect(jsonPath("$.success").value(true));
//    }
//
//    @Test
//    public void testLogin_Failure() throws Exception {
//        LoginRequestDto loginRequest = new LoginRequestDto();
//        loginRequest.setUsername("shreyas123");
//        loginRequest.setPassword("wrongpass");
//
//        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
//                .thenThrow(new RuntimeException("Invalid"));
//
//        mockMvc.perform(post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isUnauthorized()) // HTTP status for authentication failure
//                .andExpect(jsonPath("$.message").value("Invalid username or password"))
//                .andExpect(jsonPath("$.success").value(false));
//    }
//}
