package com.brokenhills.sandboxserver.security;

import net.iharder.Base64;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class CryptoService {

    public KeyPair generateKeyPair(String userSeed) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(Base64.decode(userSeed));
            keyPairGenerator.initialize(2048, random);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(String.format("Error while generating keypair: %s", e.getMessage()));
        }
    }
}
