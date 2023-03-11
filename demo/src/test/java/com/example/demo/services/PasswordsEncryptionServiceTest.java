package com.example.demo.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;

import static org.junit.jupiter.api.Assertions.*;

class PasswordsEncryptionServiceTest {

	private PasswordsEncryptionService passwordsEncryptionService = new PasswordsEncryptionService();
	private String encryptedPassword;
	private Key key;

	@BeforeEach
	void init() {
		encryptedPassword = "dGVzdA==";
		key = passwordsEncryptionService.generateKey("test");
	}

	@Test
	void shouldReturnCorrectEncryptedPassword() {

		assertEquals(passwordsEncryptionService.encrypt("test", key), encryptedPassword);
	}

	@Test
	void shouldReturnCorrectDecryptedPassword() {

		assertEquals(passwordsEncryptionService.decrypt(encryptedPassword, key), "test");
	}


}