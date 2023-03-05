package com.example.demo.services;

import com.example.demo.repository.UserRepository;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.ExceptionHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.StringEncoder;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
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
import java.util.stream.Collectors;

import static javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA512;

@Service
public class EncryptionService {

    private final UserRepository userRepository;

    private EncryptDecrypt

    public void decryptPasswordsSHA(String username) {
        List<Pair<String, String>> userPasswords = userRepository.findByUsername(username).orElseThrow(() -> new ExceptionHandler("sample")).getUserPasswords()
                .stream()
                .map(userPasswordsEntity -> Pair.of(userPasswordsEntity.getName(), userPasswordsEntity.getPassword()))
                .map(password -> Pair.of(jasyptSHA512StringEncryptor.decrypt(password.getSecond()), password.getFirst()))
                .toList();
    }

    public void decryptPasswordsHMAC(String username) {
        List<Pair<String, String>> userPasswords = userRepository.findByUsername(username).orElseThrow(() -> new ExceptionHandler("sample")).getUserPasswords()
                .stream()
                .map(userPasswordsEntity -> Pair.of(userPasswordsEntity.getName(), userPasswordsEntity.getPassword()))
                .map(password -> Pair.of(hmacJasyptSpringEncryptor.decrypt(password.getSecond()), password.getFirst()))
                .toList();
    }

    public UserEntity encrypt(UserEntity userEntity, String algorithm) {
         switch(algorithm) {
            case "SHA512" -> {
                userEntity.setPassword(jasyptSHA512StringEncryptor.encrypt(userEntity.getPassword()));
                return userRepository.save(userEntity);
            }
            case "HMAC" ->  {
                userEntity.setPassword(hmacJasyptSpringEncryptor.encrypt(userEntity.getPassword()));
                return userRepository.save(userEntity);
            }
            default -> throw new ExceptionHandler("provided wrong encryption");
        }
    }

    public UserEntity saveNewUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
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
            sha512Hmac = Mac.getInstance(HMAC_SHA512);
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
