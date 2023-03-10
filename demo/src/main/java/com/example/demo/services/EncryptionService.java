package com.example.demo.services;

import com.example.demo.config.EncryptionType;
import com.example.demo.dto.CredentialDTO;
import com.example.demo.entities.UserPasswordsEntity;
import com.example.demo.mapper.PasswordMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.ExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA512;

@Slf4j
@RequiredArgsConstructor
@Service
public class EncryptionService {

    public static final String RANDOM_PHRASE = "sample";
    private final UserRepository userRepository;
    private final PasswordsEncryptionService passwordsEncryptionService = new PasswordsEncryptionService();

    public UserEntity encryptUserPassword(CredentialDTO credentialDTO) {
        String username = SessionUtilsService.getSessionUserName();
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new ExceptionHandler("sample"));
        UserPasswordsEntity userPasswordsEntity = PasswordMapper.INSTANCE.mapToPasswordEntity(credentialDTO);
        userPasswordsEntity.setPassword(passwordsEncryptionService.encrypt(userPasswordsEntity.getPassword(), passwordsEncryptionService.generateKey(userEntity.getDecryptionKey())));
        userEntity.getUserPasswords().add(userPasswordsEntity);

       return userRepository.save(userEntity);
    }

    public List<UserPasswordsEntity> decryptUserPasswords() {
        String username = SessionUtilsService.getSessionUserName();
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        userEntity.getUserPasswords()
                .stream()
                .forEach(password -> {
                    password.setPassword(passwordsEncryptionService.decrypt(password.getPassword(), passwordsEncryptionService.generateKey(userEntity.getDecryptionKey())));
                });

        return userEntity.getUserPasswords();
    }


    public String encrypt(String password, String decryptionKey, EncryptionType algorithm) {
        switch(algorithm.toString()) {
            case "HMAC" -> {
              return calculateHMAC(password, decryptionKey);
            }
            case "SHA512" -> {
              return calculateSHA512(password);
            }

            default -> throw new ExceptionHandler("Wrong provided encryption");
        }

    }

    public static String calculateSHA512(String text) {
        try {
            //get an instance of SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            //calculate message digest of the input string - returns byte array
            byte[] messageDigest = md.digest(text.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            StringBuilder hashtext = new StringBuilder(no.toString(16));

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }

            // return the HashText
            return hashtext.toString();
        }

        // If wrong message digest algorithm was specified
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calculateHMAC(String text, String key){
        Mac sha512Hmac;
        String result="";
        try {
            final byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
            sha512Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            sha512Hmac.init(keySpec);
            byte[] macData =
                    sha512Hmac.doFinal(text.getBytes(StandardCharsets.UTF_8));
            result = Base64.getEncoder().encodeToString(macData);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

}
