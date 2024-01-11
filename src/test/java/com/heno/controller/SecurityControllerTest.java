package com.heno.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heno.config.JwtCore;
import com.heno.dto.SignInDto;
import com.heno.dto.SignUpDto;
import com.heno.repository.UserRepository;
import com.heno.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Unit tests for the SecurityController class.
 */
@ExtendWith(MockitoExtension.class)
class SecurityControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtCore jwtCore;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserService userService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityController securityController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // Initialize the MockMvc instance for testing
        mockMvc = standaloneSetup(securityController).build();
    }

    /**
     * Tests a successful sign-in process.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void signIn_Success() throws Exception {
        // Test data
        String username = "testUser";
        String password = "testPassword";
        String jwtToken = "testJwtToken";

        // Create a Sign In DTO with test credentials
        SignInDto signInDto = new SignInDto(username, password);

        doReturn(jwtToken).when(userService).loginUser(signInDto);
        // Perform the HTTP request to the sign-in endpoint and verify the expected outcome
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signInDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(jwtToken));
    }

    /**
     * Tests an unsuccessful sign-in due to invalid credentials.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void signIn_Unauthorized() throws Exception {
        // Test data
        String username = "testUser";
        String password = "incorrectPassword";

        // Create a Sign In DTO with invalid credentials
        SignInDto signInDto = new SignInDto(username, password);

        // Mock the behavior of the Authentication Manager to throw BadCredentialsException
        doThrow(BadCredentialsException.class).when(userService).loginUser(signInDto);

        // Perform the HTTP request to the sign-in endpoint and verify the expected unauthorized status
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signInDto)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests a successful sign-up process.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void signUp_Success() throws Exception {
        // Create a Sign-Up DTO with new user credentials
        SignUpDto signUpDto = new SignUpDto("email","fio","number","newUser", "password", Collections.emptySet());


        // Mock the behavior of the UserRepository to indicate that the username does not exist
        doNothing().when(userService).addUser(signUpDto);

        // Perform the HTTP request to the sign-up endpoint and verify the expected success status
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Success signUp!"));
    }

    /**
     * Tests an unsuccessful sign-up due to an existing user.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void signUp_UserAlreadyExists() throws Exception {
        // Create a Sign-Up DTO with credentials of an existing user
        SignUpDto signUpDto = new SignUpDto("email","fio","number","existingUser", "password", Collections.emptySet());

        // Mock the behavior of the UserRepository to indicate that the username already exists
        doThrow(new RuntimeException("Choose a different name")).when(userService).addUser(signUpDto);

        // Perform the HTTP request to the sign-up endpoint and verify the expected bad request status
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signUpDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Choose a different name"));
    }
}
