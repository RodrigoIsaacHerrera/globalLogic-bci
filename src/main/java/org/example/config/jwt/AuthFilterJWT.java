package org.example.config.jwt;

import io.jsonwebtoken.JwtException;
import org.example.service.JwtService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class AuthFilterJWT extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthFilterJWT(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String token = getTokenRequest(request);
        final String username;

        if(token == null){
            filterChain.doFilter(request,response);
            return;
        }
        try {
            username = jwtService.getUsernameFromToken(token);
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException(" Bad Operation ");
        }
        if (username !=null && SecurityContextHolder.getContext().getAuthentication()==null) {

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if(jwtService.isTokenValid(token, userDetails)){
                    UsernamePasswordAuthenticationToken permissionToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    permissionToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                }
            } catch (JwtException e) {
                throw new JwtException(" Bad Token ");
            } catch (UsernameNotFoundException u){
                throw new UsernameNotFoundException(u.getMessage());
            }
        }

        filterChain.doFilter(request,response);

    }

    private String getTokenRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")){

                return authHeader.substring(7);
        }
        return null;
    }
}
