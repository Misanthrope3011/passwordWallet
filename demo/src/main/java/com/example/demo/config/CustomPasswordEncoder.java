package com.example.demo.config;

import com.example.demo.entities.UserEntity;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class CustomPasswordEncoder implements PasswordEncoder {

	private UserEntity entity;

	@Override
	public String encode(CharSequence rawPassword) {
		return null;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return false;
	}

	@Override
	public boolean upgradeEncoding(String encodedPassword) {
		return PasswordEncoder.super.upgradeEncoding(encodedPassword);
	}

	public CustomPasswordEncoder(UserEntity entity) {
		this.entity = entity;
	}

}
