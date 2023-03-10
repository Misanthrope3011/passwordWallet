package com.example.demo.services;

import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.ExceptionHandler;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public Optional<UserEntity> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public boolean isUserExists(String username) {
		return userRepository.findByUsername(username).isPresent();
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
