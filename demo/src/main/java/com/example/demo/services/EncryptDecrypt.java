package com.example.demo.services;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
public class EncryptDecrypt {
	private static final String ALGO = "AES";
	private static final byte[] keyValue
			= new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};
	//encrypts string and returns encrypted string
	public String encrypt(String data, Key key) throws Exception {
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(data.getBytes());
		return Base64.getEncoder().encodeToString(encVal);
	}
	//decrypts string and returns plain text
	public String decrypt(String encryptedData, Key key) throws Exception {
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
		byte[] decValue = c.doFinal(decodedValue);
		return new String(decValue);
	}
	// Generate a new encryption key.
	private static Key generateKey() throws Exception {
		return new SecretKeySpec(keyValue, ALGO);
	}

}
