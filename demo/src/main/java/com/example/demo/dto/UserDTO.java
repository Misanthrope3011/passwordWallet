package com.example.demo.dto;

public record UserDTO(String username, String password) {

	@Override
	public String username() {
		return username;
	}

	@Override
	public String password() {
		return password;
	}


}
