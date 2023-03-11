package com.example.demo.services;

import com.example.demo.entities.UserEntity;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findByUsername_shouldInvokeFindByUser() {

        userService.findByUsername("USERNAME");

        Mockito.verify(userRepository).findByUsername(any());
    }

    @Test
    void shouldReturnFalse_givenTwoEqualsUsernames() {

        userService.isUserUnique(new UserEntity());

        Mockito.verify(userRepository).findByUsername(any());
    }

}