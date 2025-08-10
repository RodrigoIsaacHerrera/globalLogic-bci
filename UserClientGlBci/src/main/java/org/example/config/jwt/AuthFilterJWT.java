package org.example.config.jwt;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String TOKEN = getTokenRequest(request);

        if(TOKEN == null){
            filterChain.doFilter(request,response);
            return;
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
