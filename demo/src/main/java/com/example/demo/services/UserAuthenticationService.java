package com.example.demo.services;

import com.example.demo.config.CustomPasswordEncoder;
import com.example.demo.config.EncryptionType;
import com.example.demo.dto.UserDTO;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.ExceptionHandler;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.example.demo.services.EncryptionService.RANDOM_PHRASE;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthenticationService {

	private final EncryptionService encryptionService;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;

	public String signUpUser(UserEntity userEntity, EncryptionType encryptionType) {
		String decryptionKey = userEntity.getDecryptionKey() == null ? RANDOM_PHRASE.concat(UUID.randomUUID().toString()) : userEntity.getDecryptionKey();
		userEntity.setDecryptionKey(decryptionKey);
		userEntity.setEncryptionType(encryptionType);

		try {
			String encrypted = encryptionService.encrypt(userEntity.getPassword(), userEntity.getDecryptionKey(), userEntity.getEncryptionType());
			userEntity.setPassword(encrypted);
			userRepository.save(userEntity);
			return objectMapper.writeValueAsString(userEntity);
		} catch (JsonProcessingException ex) {
			throw new ExceptionHandler("Error parsing json");
		}
	}

	public ResponseEntity<Object> performLogin(HttpServletRequest request, UserDTO userDTO, UserEntity entity) {
		new CustomPasswordEncoder(entity);
		String encryptedPassword = encryptionService.encrypt(userDTO.password(), entity.getDecryptionKey(), entity.getEncryptionType());

		if(encryptedPassword.equals(entity.getPassword())) {
			authenticateUser(request, userDTO, entity);

			return ResponseEntity.ok("Logged in");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
		}
	}

	public void authenticateUser(HttpServletRequest request, UserDTO userDTO, UserEntity entity) {
		Authentication authReq = new UsernamePasswordAuthenticationToken(userDTO.username(), userDTO.password(), entity.getAuthorities());
		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(authReq);
		HttpSession session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", sc);
	}

}
