package com.example.demo.services;

import com.example.demo.exceptions.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.binary.AES256BinaryEncryptor;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.logging.Logger;

@Slf4j
public class PasswordsEncryptionService {

	private static final String ALGO = "AES";

	public String encrypt(String data, Key key) {
		try {
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.ENCRYPT_MODE, key);
			return Base64.getEncoder().encodeToString(data.getBytes());
		} catch(Exception ex) {
			throw new ExceptionHandler(ex.getMessage());
		}
	}
	//decrypts string and returns plain text
	public String decrypt(String encryptedData, Key key) {
		try {
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
			return new String(decodedValue);
		} catch(Exception ex) {
			throw new ExceptionHandler("Problem with decryption found");
		}

	}

	public Key generateKey(String keyValue) {
		byte[] fixedBites = new byte[16];
		byte[] keyBytes = keyValue.getBytes();

		for (int i = 0; i < 16; i++) {
			if(i < keyBytes.length)
				fixedBites[i] = keyBytes[i];
			else fixedBites[i] = 0;
		}

		return new SecretKeySpec(fixedBites, ALGO);
	}

}



