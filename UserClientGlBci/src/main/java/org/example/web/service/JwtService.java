package org.example.web.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {


    public String generateRecordToken(UserDetails user) {
        return getSignUpToken(new HashMap<>(), user);
    }


/*    {
            "id":"e5c6cf84-8860-4c00-91cd-22d3be28904e",
            "created":"Nov 16, 2021 12:51:43 PM",
            "lastLogin":"Nov 16, 2021 12:51:43 PM",
            "token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWxpb0B0ZXN0...",
            "isActive":true,
       }*/


    private String getSignUpToken(Map<String, Object> exClaims, UserDetails user) {
        String dateOrigin = new Date(System.currentTimeMillis()).toString();
        exClaims.put("id",user.getUsername());
        exClaims.put("created", dateOrigin);
        exClaims.put("lastLogin", dateOrigin);
        return Jwts.builder()
                .setClaims(exClaims)
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // válido por 1 día
                .signWith(getKey(getSecret(user.getUsername())), SignatureAlgorithm.HS256)
                .compact();
    }

    /*    {
            "id":"e5c6cf84-8860-4c00-91cd-22d3be28904e",
            "created":"Nov 16, 2021 12:51:43 PM",
            "lastLogin":"Nov 16, 2021 12:51:43 PM",
            "token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWxpb0B0ZXN0...",
            "isActive":true,
            "name":"Julio Gonzalez",
            "email": "julio@testssw.cl",
            "password": "a2asfGfdfdf4",
            "phones":[{
                "number":87650009,
                "citycode":7,
                "contrycode":"25"
             }]
    }*/
    private String getLoginToken(Map<String, Object> exClaims) {
        return Jwts.builder().setClaims(exClaims).toString();
    }

    protected static String getSecret(String uuid){
        return uuid.replace('-','0');
    };
    protected static SecretKey getKey(String secret){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    };
}
