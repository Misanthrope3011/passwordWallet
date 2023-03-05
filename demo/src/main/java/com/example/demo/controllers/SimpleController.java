package com.example.demo.controllers;

import com.example.demo.dto.CredentialDTO;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.UserPasswordsEntity;
import com.example.demo.services.EncryptionService;
import com.example.demo.services.SessionUtilsService;
import com.example.demo.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimpleController {

    private final EncryptionService encryptionService;
    private final ConfigurableEnvironment environment;
    private final UserService userService;


    @PutMapping("/user/changePassword/{encryptionType}")
    public ResponseEntity<Object> encryptPassword(@PathVariable String encryptionType, @RequestBody CredentialDTO credentialDTO) {
        String currentUser = SessionUtilsService.getSessionUserName();
        UserEntity userEntity = userService.findByUsername(currentUser);

        return ResponseEntity.ok(new Object());
    }

    @PostMapping("/user/addCrendentials")
    public ResponseEntity<Object> addCredentials(@RequestBody UserPasswordsEntity userPasswordsEntity) {
        String currentUser = SessionUtilsService.getSessionUserName();
        UserEntity userEntity = userService.findByUsername(currentUser);

        return ResponseEntity.ok(new Object());
    }

    @PostMapping("/login")
    public ResponseEntity<Object> sampleResponse() {

        return ResponseEntity.ok(new Object());
    }

    @PostMapping("/signup/{encryption}")
    public ResponseEntity<Object> signUp(@RequestBody UserEntity user, @PathVariable String encryption) {

        return ResponseEntity.ok(encryptionService.encrypt(user, encryption));
    }

}
