package com.example.demo.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordEncryptor {

	private final PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
	private final SimpleStringPBEConfig config = new SimpleStringPBEConfig();


	@Bean("jasyptSHA512StringEncryptor")
		public StringEncryptor SHA512Encryptor() {
			config.setPassword("mySecretKey");
			config.setAlgorithm("SHA512");
			config.setKeyObtentionIterations("1000");
			config.setPoolSize("1");
			config.setProviderName("SunJCE");
			config.setStringOutputType("base64");
			encryptor.setConfig(config);
			return encryptor;
	}

	@Bean("hmacJasyptSpringEncryptor")
	public StringEncryptor hmacEncryptor() {
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword("mySecretKey");
		config.setAlgorithm("PBKDF2WithHmacSHA256");
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("base64");
		encryptor.setConfig(config);
		return encryptor;
	}

}
