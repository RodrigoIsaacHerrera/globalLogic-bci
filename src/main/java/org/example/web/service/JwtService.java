package org.example.web.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.data.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    final String secret = "586E3272357578982F413F4428472B4B6250655368566B598071733676397924";

    public String generateToken(User user) {

        return getToken(new HashMap<>(), user);
    }

    String getToken(Map<String, Object> exClaims, User user) {
        String dateOrigin = new Date(System.currentTimeMillis()).toString();

        exClaims.put("isActive", true);
        exClaims.put("lastLogin", dateOrigin);
        exClaims.put("created", dateOrigin);
        exClaims.put("id", user.getId().toString());

        return Jwts.builder()
                .setId(user.getId().toString())
                .setClaims(exClaims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getKey(),SignatureAlgorithm.HS256).compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
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
