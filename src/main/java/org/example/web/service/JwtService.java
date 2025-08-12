package org.example.web.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.data.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {


    public String generateRecordToken(User user) {
        return getToken(new HashMap<>(), user);
    }

    public String generateLoginToken(User user) {
        return getToken(new HashMap<>(), user);
    }

    String getToken(Map<String, Object> exClaims, User user) {
        String dateOrigin = new Date(System.currentTimeMillis()).toString();

        exClaims.put("isActive", true);
        exClaims.put("lastLogin", dateOrigin);
        exClaims.put("created", dateOrigin);
        exClaims.put("id", user.getId().toString());



        return Jwts.builder()
                .setClaims(exClaims)
                .setSubject(user.getEmail())
                .signWith(getKey(user.getIdString()), SignatureAlgorithm.HS256).compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public String getIdFromToken(String token) {
        return getClaim(token, Claims::getId);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey(getIdFromToken(token)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getKey(String usernameFromToken) {
        return Keys.hmacShaKeyFor(usernameFromToken.getBytes(StandardCharsets.UTF_8));
    }


    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

}
