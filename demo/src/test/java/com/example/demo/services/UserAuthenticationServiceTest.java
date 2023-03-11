package com.example.demo.services;

import com.example.demo.config.EncryptionType;
import com.example.demo.dto.UserDTO;
import com.example.demo.entities.UserEntity;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class UserAuthenticationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private HttpServletRequest servletRequest;

	@Mock
	HttpSession httpSession;

	@Mock
	private EncryptionService encryptionService;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private UserAuthenticationService authenticationService;

	@Test
	void signUpUser() {
		authenticationService.signUpUser(new UserEntity(), EncryptionType.SHA512);

		Mockito.verify(userRepository).save(any(UserEntity.class));
	}

	@Test
	void performLoginInvalidCredentials() {
		Mockito.when(encryptionService.encrypt(any(), any(), any())).thenReturn("encryptedPass");
		UserDTO userDTO = new UserDTO("sample", "password");
		UserEntity entity = new UserEntity();
		entity.setPassword("notMatchingPassword");

		ResponseEntity<Object> loginCallback = authenticationService.performLogin(servletRequest, userDTO, entity);

		assertEquals(loginCallback.getStatusCode().value(), HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	void performLoginValidCredentials() {
		Mockito.when(encryptionService.encrypt(any(), any(), any())).thenReturn("encryptedPass");
		Mockito.when(servletRequest.getSession(eq(true))).thenReturn(httpSession);
		UserDTO userDTO = new UserDTO("sample", "smple");
		UserEntity entity = new UserEntity();
		entity.setPassword("encryptedPass");

		ResponseEntity<Object> loginCallback = authenticationService.performLogin(servletRequest, userDTO, entity);

		assertEquals(loginCallback.getStatusCode().value(), HttpStatus.OK.value());
	}

}