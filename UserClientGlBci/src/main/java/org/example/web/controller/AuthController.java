package org.example.web.controller;


import lombok.RequiredArgsConstructor;
import org.example.config.jwt.AccessRequest;
import org.example.config.jwt.SignUpRequest;
import org.example.web.reponse.CustomSignUpRes;
import org.example.web.reponse.LoginResponse;
import org.example.web.reponse.SignUpResponse;
import org.example.web.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AccessRequest loginRequest){

        return ResponseEntity.ok(this.authService.login(loginRequest));
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<SignUpResponse> register(@RequestBody SignUpRequest registerRequest){
        return ResponseEntity.ok(this.authService.signUp(registerRequest));
    }
}
