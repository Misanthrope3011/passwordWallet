package com.example.demo.controllers;

import com.example.demo.config.EncryptionType;
import com.example.demo.dto.CredentialDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entities.UserEntity;
import com.example.demo.services.EncryptionService;
import com.example.demo.services.SessionUtilsService;
import com.example.demo.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimpleController {

    private final EncryptionService encryptionService;
    private final UserService userService;

    @PutMapping("/user/changePassword/{encryptionType}")
    public ResponseEntity<UserEntity> encryptPassword(@PathVariable String encryptionType, @RequestBody CredentialDTO credentialDTO) {
        String currentUser = SessionUtilsService.getSessionUserName();
        UserEntity userEntity = userService.findByUsername(currentUser);
        userEntity.setPassword(encryptionService.encrypt(credentialDTO.password(), userEntity.getDecryptionKey(), EncryptionType.valueOf(encryptionType)));

        return ResponseEntity.ok(userService.saveNewUser(userEntity));
    }

    @PostMapping("/user/addCredentials")
    public ResponseEntity<Object> addCredentials(@RequestBody CredentialDTO credentialDTO) {

        return ResponseEntity.ok(encryptionService.encryptUserPassword(credentialDTO));
    }

    @GetMapping("/user/decode")
    public ResponseEntity<Object> decode() {

        return ResponseEntity.ok(encryptionService.decryptUserPasswords());
    }

    @PostMapping("/login")
    public ResponseEntity<Object> sampleResponse(HttpServletRequest request, @RequestBody UserDTO userDTO) {
        UserEntity entity = userService.findByUsername(userDTO.username());
        String encryptedPassword = encryptionService.encrypt(userDTO.password(), entity.getDecryptionKey(), entity.getEncryptionType());
        if(encryptedPassword.equals(entity.getPassword())) {
            Authentication authReq = new UsernamePasswordAuthenticationToken(userDTO.username(), userDTO.password(), entity.getAuthorities());
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authReq);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", sc);

            return ResponseEntity.ok("Logged in");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/signup/{encryption}")
    public ResponseEntity<Object> signUp(@RequestBody UserEntity user, @PathVariable String encryption) {

        if (!userService.isUserExists(user.getUsername())) {
            EncryptionType encryptionType = EncryptionType.valueOf(encryption);

            return ResponseEntity.ok(encryptionService.signUpUser(user, encryptionType));
        }

        return ResponseEntity.status(409).body("User already exists");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest servletRequest) {
        SecurityContextHolder.getContext().setAuthentication(null);

        return ResponseEntity.noContent().build();
    }

}
