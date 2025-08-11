package org.example.web.reponse;

public class CustomSignUpRes {


    String id, created, lastLogin, token, password;
    boolean isActive;

    public CustomSignUpRes(SignUpResponse signUpResponse){
        this.id = signUpResponse.getId();
        this.created = signUpResponse.getCreated();
        this.lastLogin = signUpResponse.getLastLogin();
        this.token = signUpResponse.getToken();
        this.password = signUpResponse.getPassword();
        this.isActive = signUpResponse.isActive();
    }
}
