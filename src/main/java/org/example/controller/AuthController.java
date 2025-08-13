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
    private final ValidationsService validationsService;

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
   String evaluation = validationsService.validationParams(loginRequest.getEmail(),
           loginRequest.getPassword());
        if (evaluation.contains("false")) {
            throw new IllegalArgumentException(" "+ evaluation);
        }
        validationsService.validationParams(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(this.authService.login(loginRequest));
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<SignUpResponse> register(@RequestBody SignUpRequest registerRequest) {
        String evaluation = validationsService.validationParams(registerRequest.getEmail(),
                registerRequest.getPassword());
        if (evaluation.contains("false")) {
            throw new IllegalArgumentException(" "+ evaluation);
        }

        return ResponseEntity.ok(this.authService.signUp(registerRequest));
    }
}