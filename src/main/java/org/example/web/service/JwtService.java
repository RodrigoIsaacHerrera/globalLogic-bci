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
        return getSignUpToken(new HashMap<>(), user);
    }

    public String generateLoginToken(User user) {
        return getLoginToken(new HashMap<>(), user);
    }

    String getSignUpToken(Map<String, Object> exClaims, User user) {
        String dateOrigin = new Date(System.currentTimeMillis()).toString();
        String id = "id";
        exClaims.put("isActive", true);
        exClaims.put("lastLogin", dateOrigin);
        exClaims.put("created", dateOrigin);
        exClaims.put("id", user.getId());

        return Jwts.builder()
                .setId(exClaims.get(id).toString())
                .setClaims(exClaims)
                .setSubject(user.getIdString())
                .signWith(Keys.hmacShaKeyFor(user.getIdString().getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256).compact();
    }


    String getLoginToken(Map<String, Object> exClaims, User user) {
        String dateLogin = new Date(System.currentTimeMillis()).toString();
        String id = "id";
        exClaims.put("isActive", true);
        exClaims.put("lastLogin", dateLogin);
        exClaims.put("created", dateLogin); // debe ser cambiado aun base de datos que registre session traffic
        exClaims.put("id", user.getId());
        return Jwts.builder()
                .setClaims(exClaims).
                signWith(Keys.hmacShaKeyFor(user.getIdString().getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256).compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey(getUsernameFromToken(token)))
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
