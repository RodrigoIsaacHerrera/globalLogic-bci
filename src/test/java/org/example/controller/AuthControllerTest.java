package org.example.controller;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import org.example.data.mappers.UserMapper;
import org.example.data.mappers.UserMapper.UserMapperBuilder;
import org.example.service.AuthService;
import org.example.service.ValidationsService;
import org.example.web.reponse.LoginResponse;
import org.example.web.reponse.SignUpResponse;
import org.example.web.request.LoginRequest;
import org.example.web.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {AuthController.class, GlobalExceptionHandler.class})
@ExtendWith(SpringExtension.class)
class AuthControllerTest {
    @Autowired
    private AuthController authController;

    @MockBean
    private AuthService authService;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @MockBean
    private ValidationsService validationsService;

    /**
     * Test {@link AuthController#login(LoginRequest)}.
     *
     * <ul>
     *   <li>Then status {@link StatusResultMatchers#isOk()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthController#login(LoginRequest)}
     */
    @Test
    @DisplayName("Test login(LoginRequest); then status isOk()")
    void testLogin_thenStatusIsOk() throws Exception {
        // Arrange
        when(authService.login(Mockito.<LoginRequest>any()))
                .thenReturn(
                        new LoginResponse(
                                "42",
                                "Jan 1, 2020 8:00am GMT+0100",
                                "Last Login",
                                "ABC123",
                                true,
                                "Name",
                                "jane.doe@example.org",
                                "iloveyou",
                                new ArrayList<>()));
        when(validationsService.validationParams(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn("Validation Params");
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("jane.doe@example.org");
        loginRequest.setPassword("iloveyou");
        String content = new ObjectMapper().writeValueAsString(loginRequest);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string(
                                        "{\"id\":\"42\",\"created\":\"Jan 1, 2020 8:00am GMT+0100\",\"lastLogin\":\"Last Login\",\"token\":\"ABC123\",\"name\":"
                                                + "\"Name\",\"email\":\"jane.doe@example.org\",\"password\":\"iloveyou\",\"phones\":[],\"active\":true}"));
    }

    /**
     * Test {@link AuthController#signUp(SignUpRequest)}.
     *
     * <ul>
     *   <li>Then status {@link StatusResultMatchers#isOk()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthController#signUp(SignUpRequest)}
     */
    @Test
    @DisplayName("Test register(SignUpRequest); then status isOk()")
    void testSignUp_thenStatusIsOk() throws Exception {
        // Arrange
        UserMapperBuilder passwordResult =
                UserMapper.builder()
                        .email("jane.doe@example.org")
                        .id("42")
                        .name("Name")
                        .password("iloveyou");
        UserMapper user = passwordResult.phones(new ArrayList<>()).build();
        when(authService.signUp(Mockito.<SignUpRequest>any()))
                .thenReturn(
                        new SignUpResponse(
                                user, "42", "Jan 1, 2020 8:00am GMT+0100", "Last Login", "ABC123", true));
        when(validationsService.validationParams(Mockito.<String>any(), Mockito.<String>any()))
                .thenReturn("Validation Params");

        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("jane.doe@example.org");
        signUpRequest.setName("Name");
        signUpRequest.setPassword("iloveyou");
        signUpRequest.setPhones(new ArrayList<>());
        String content = new ObjectMapper().writeValueAsString(signUpRequest);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string(
                                        "{\"user\":{\"id\":\"42\",\"name\":\"Name\",\"email\":\"jane.doe@example.org\",\"password\":\"iloveyou\",\"phones\":[]"
                                                + "},\"id\":\"42\",\"created\":\"Jan 1, 2020 8:00am GMT+0100\",\"lastLogin\":\"Last Login\",\"token\":\"ABC123\","
                                                + "\"active\":true}"));
    }
}
