package org.example.config.security;

import lombok.RequiredArgsConstructor;
import org.example.config.jwt.AuthFilterJWT;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final AuthFilterJWT jwtAccessFilter;
    
    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity.csrf(
                csrf -> csrf.disable()
                )
                .authorizeHttpRequests(authRequest ->
                        authRequest.antMatchers("/auth/**").permitAll()
                                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
                ).sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAccessFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

}
