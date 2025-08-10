package org.example.web.controller;


import lombok.RequiredArgsConstructor;
import org.example.config.jwt.AccessRequest;
import org.example.config.jwt.AuthResponse;
import org.example.config.jwt.SignUpRequest;
import org.example.web.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService loginService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AccessRequest loginRequest){

        return ResponseEntity.ok(this.loginService.login(loginRequest));
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<AuthResponse> register(@RequestBody SignUpRequest registerRequest){
        return ResponseEntity.ok(this.loginService.register(registerRequest));
    }
}
