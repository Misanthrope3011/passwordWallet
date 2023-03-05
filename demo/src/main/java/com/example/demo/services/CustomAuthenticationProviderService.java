package com.example.demo.services;

import com.example.demo.exceptions.ExceptionHandler;
import com.example.demo.repository.UserRepository;
import com.example.demo.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class CustomAuthenticationProviderService implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new ExceptionHandler("No user for creds"));

        String secret = generateSecret(user);

        return new UsernamePasswordAuthenticationToken(username, secret, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }

    private String generateSecret(UserEntity user) {
        byte[] key = user.getKey().getBytes(StandardCharsets.UTF_8);
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key).hmacHex(user.getUsername());
    }

}
