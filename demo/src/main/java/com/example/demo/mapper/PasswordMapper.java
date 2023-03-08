package com.example.demo.mapper;

import com.example.demo.dto.CredentialDTO;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.UserPasswordsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PasswordMapper {

	PasswordMapper INSTANCE = Mappers.getMapper(PasswordMapper.class);

	CredentialDTO mapToCredentialDTO(UserPasswordsEntity user);

	@Mapping(source = "url", target = "url")
	@Mapping(source = "name", target = "name")
	@Mapping(source = "username", target = "username")
	UserPasswordsEntity mapToPasswordEntity(CredentialDTO credentialDTO);

}
