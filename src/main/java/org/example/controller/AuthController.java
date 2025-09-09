package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.ValidationsService;
import org.example.web.request.LoginRequest;
import org.example.web.request.SignUpRequest;
import org.example.web.reponse.LoginResponse;
import org.example.web.reponse.SignUpResponse;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest, @RequestHeader("Authorization")
                                               String authorizationHeader) {
        return ResponseEntity.ok(this.authService.login(loginRequest, authorizationHeader));
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest registerRequest) {
        return ResponseEntity.ok(this.authService.signUp(registerRequest));
    }
}