package com.example.demo.services;

import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.ExceptionHandler;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public UserEntity findByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(() -> new ExceptionHandler("no such username"));
	}

	public boolean isUserUnique(UserEntity entity) {
		return userRepository.findByUsername(entity.getUsername()).isPresent();
	}

	public UserEntity saveNewUser(UserEntity userEntity) {
		return userRepository.save(userEntity);
	}

	public UserEntity createNewUser(UserEntity entity) {
		return userRepository.save(entity);
	}

}
