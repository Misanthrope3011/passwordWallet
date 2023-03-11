package com.example.demo.services;

import com.example.demo.config.EncryptionType;
import com.example.demo.dto.CredentialDTO;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.UserPasswordsEntity;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncryptionServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private SecurityContext securityContext;

	@Mock
	private Authentication authentication;

	@Captor
	private ArgumentCaptor<UserEntity> userPasswordSavedCaptor;

	@Mock
	private PasswordsEncryptionService passwordsEncryptionService;

	@Captor
	private ArgumentCaptor<UserPasswordsEntity> paswordCaptor;

	@InjectMocks
	private EncryptionService encryptionService;

	@Test
	void shouldInvokeUserPasswordSave() {
		try (MockedStatic<SecurityContextHolder> contextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
			mockSecurityContext();
			CredentialDTO credentialDTO = new CredentialDTO("password", "username", "name", "url", "sample");
			UserEntity entity = initializeUserEntity();
			when(userRepository.findByUsername(any())).thenReturn(Optional.of(entity));

			encryptionService.encryptGivenUserCredentials(credentialDTO);
			String expectedEncryptedPassword = "cGFzc3dvcmQ=";

			verify(userRepository).save(userPasswordSavedCaptor.capture());
			Assertions.assertEquals(getLastSavedPassword().getPassword(), expectedEncryptedPassword);
		}
	}

	private void mockSecurityContext() {
		when(SecurityContextHolder.getContext()).thenReturn(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		when(authentication.getName()).thenReturn("myUser");
	}

	private static UserEntity initializeUserEntity() {
		UserEntity entity = new UserEntity();
		entity.setUserPasswords(new ArrayList<>());
		entity.setDecryptionKey("KEY");
		entity.setEncryptionType(EncryptionType.SHA512);
		entity.setId(1L);
		List<UserPasswordsEntity> userPasswordsEntities = new ArrayList<>();
		userPasswordsEntities.add(new UserPasswordsEntity(1L, "assd", "afddf", "afsdf", "assds", "zds"));
		userPasswordsEntities.add(new UserPasswordsEntity(2L, "assd", "afddf", "afsdf", "assds", "zds"));
		userPasswordsEntities.add(new UserPasswordsEntity(3L, "assd", "afddf", "afsdf", "assds", "zds"));
		userPasswordsEntities.add(new UserPasswordsEntity(4L, "assd", "afddf", "afsdf", "assds", "zds"));

		return entity;
	}

	private UserPasswordsEntity getLastSavedPassword() {
		return getCurrentUserPassword().get(getCurrentUserPassword().size() - 1);
	}

	private List<UserPasswordsEntity> getCurrentUserPassword() {
		return userPasswordSavedCaptor.getValue().getUserPasswords();
	}

	@Test
	void shouldInvokeDecryptMethodCurrentNumberOfTimes() {
		try (MockedStatic<SecurityContextHolder> contextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
			UserEntity testUser = initializeUserEntity();
			when(userRepository.findByUsername(any())).thenReturn(Optional.of(testUser));
			mockSecurityContext();

			encryptionService.decryptUserPasswords();

			verify(passwordsEncryptionService, times(testUser.getUserPasswords().size())).decrypt(any(), any());
		}
	}

	@Test
	void shouldReturnEncryptedString() {
		String result = encryptionService.encrypt("pasword", "key", EncryptionType.HMAC);
		String expectedPassword = "dShfnhzZIv8VkFjQFeVYFKhmhz/vugykWFemQEm56zDPYX1+U4zv+TDqY3cwFQyAzfk31CPDZyu8P/3zP1CE2Q==";

		Assertions.assertEquals(expectedPassword, result);
	}

	@Test
	void calculateSHA512() {
		try (MockedStatic<SecurityContextHolder> contextHolderMockedStatic = mockStatic(SecurityContextHolder.class)) {
			mockSecurityContext();
			when(userRepository.findByUsername(any())).thenReturn(Optional.of(initializeUserEntity()));

			encryptionService.encryptGivenUserCredentials(new CredentialDTO("password", "key", "desc", "sample", "desc"));
			String expectedPassword = "cGFzc3dvcmQ=";

			verify(userRepository).save(userPasswordSavedCaptor.capture());
			Assertions.assertEquals(expectedPassword, userPasswordSavedCaptor.getValue().getUserPasswords().get(userPasswordSavedCaptor.getValue().getUserPasswords().size() - 1).getPassword());
		}
	}


}