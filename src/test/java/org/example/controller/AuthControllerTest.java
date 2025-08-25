package org.example.controller;

import org.example.service.AuthService;
import org.example.service.ValidationsService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.web.request.LoginRequest;

@ContextConfiguration(classes = {AuthController.class, GlobalExceptionHandler.class})
@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@MockBean
	private ValidationsService validationsService;

	@Test
	public void signUp() throws Exception {
		this.mockMvc.perform(post("/auth/sign-up").content("abc").contentType(MediaType.APPLICATION_JSON_VALUE)).
		  andExpect(status().isOk()).
		  andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE)).
		  andExpect(jsonPath("$.user").value("<value>")).
		  andExpect(jsonPath("$.id").value("<value>")).
		  andExpect(jsonPath("$.created").value("<value>")).
		  andExpect(jsonPath("$.lastLogin").value("<value>")).
		  andExpect(jsonPath("$.token").value("<value>")).
		  andExpect(jsonPath("$.isActive").value("<value>"));
	}

	@Test
	public void login() throws Exception {
		LoginRequest loginRequest = new LoginRequest();
		String authorizationHeader = "abc";
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(authorizationHeader);
		this.mockMvc.perform(post("/auth/login").content(json).contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(jsonPath("$.id").value("<value>"))
			.andExpect(jsonPath("$.created").value("<value>"))
			.andExpect(jsonPath("$.lastLogin").value("<value>"))
			.andExpect(jsonPath("$.token").value("<value>"))
			.andExpect(jsonPath("$.isActive").value("<value>"))
			.andExpect(jsonPath("$.name").value("<value>"))
			.andExpect(jsonPath("$.email").value("<value>"))
			.andExpect(jsonPath("$.password").value("<value>"))
			.andExpect(jsonPath("$.phones[0]").value("<value>"));
	}

}
