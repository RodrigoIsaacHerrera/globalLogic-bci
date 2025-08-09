package org.example.web.controller;


import lombok.RequiredArgsConstructor;
import org.example.config.jwt.AccessRequest;
import org.example.config.jwt.AuthResponse;
import org.example.config.jwt.AuthService;
import org.example.config.jwt.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService loginService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AccessRequest loginRequest){

        return ResponseEntity.ok(this.loginService.login(loginRequest));
    }

    @PostMapping(value = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest){
        return ResponseEntity.ok(this.loginService.register(registerRequest));
    }
}
