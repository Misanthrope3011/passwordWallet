package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests(request -> {
                    request.requestMatchers("/user/**", "/login")
                            .authenticated();
                })
                .httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and().
                authorizeHttpRequests()
                .anyRequest()
                .permitAll().
                and().build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        BasicAuthenticationEntryPoint authenticationEntryPoint = new BasicAuthenticationEntryPoint();
        authenticationEntryPoint.setRealmName("hehe");
        return authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        if("XD".equals(userDetailsService.toString())) {
            return new MessageDigestPasswordEncoder("SHA512");
        } else {
            return new MessageDigestPasswordEncoder("SHA256");
        }
    }

}
