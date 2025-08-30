package org.example.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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
    //this is a bad practice, only for this time I use.
    final String secret = "586E3272357578982F413F4428472B4B6250655368566B598071733676397924";

    public String generateToken(User user) {

        return getToken(new HashMap<>(), user);
    }

    String getToken(Map<String, Object> exClaims, User user) {
        String dateOrigin = new Date(System.currentTimeMillis()).toString();

        exClaims.put("isActive", true);
        exClaims.put("lastLogin", dateOrigin);
        exClaims.put("created", dateOrigin);
        exClaims.put("Id", user.getId().toString());

        return Jwts.builder()
                .setClaims(exClaims)
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getKey(),SignatureAlgorithm.HS256).compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public String getIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("Id", String.class);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = getUsernameFromToken(token);
            if (isTokenExpired(token)) {
                throw new JwtException("Expired Token");
            }
            return username.equals(userDetails.getUsername());
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("Invalid Token", e);
        } catch (JwtException e) {
            throw new JwtException("Expired Token", e);
        }
    }

    private Claims getAllClaims(String token) throws SignatureException {
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


    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) throws MalformedJwtException {
        final Claims claims;
        try{ claims = getAllClaims(token);} catch (MalformedJwtException e) {
            throw new MalformedJwtException(" Not Allowed Credentials");
        }

        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {

        return getExpiration(token).before(new Date());
    }

}
