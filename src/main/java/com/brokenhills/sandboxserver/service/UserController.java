package com.brokenhills.sandboxserver.service;

import com.brokenhills.sandboxserver.model.UserRequest;
import com.brokenhills.sandboxserver.model.UserStatus;
import com.brokenhills.sandboxserver.security.CryptoService;
import com.brokenhills.sandboxserver.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.KeyPair;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CryptoService cryptoService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UserController(UserService userService,
                          CryptoService cryptoService,
                          AuthenticationManager authenticationManager,
                          TokenService tokenService) {
        this.userService = userService;
        this.cryptoService = cryptoService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createUser(RequestEntity<UserRequest> credentials) {
        try {
            userService.createUser(Objects.requireNonNull(credentials.getBody()).getLogin(),
                    credentials.getBody().getPassword(), UserStatus.USER);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating user!", e);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> generateAuthenticationToken(@RequestBody UserRequest authenticationRequest) {
        try {
            authenticate(authenticationRequest.getLogin(), authenticationRequest.getPassword());

            final UserDetails userDetails = userService
                    .loadUserByUsername(authenticationRequest.getLogin());

            final String token = tokenService.generateToken(userDetails);

            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/keypair")
    public ResponseEntity<Map<String, byte[]>> getKeyPair(RequestEntity<Map<String, String>> keyPairRequest) {
        try {
            KeyPair keyPair = cryptoService
                    .generateKeyPair(
                            Objects.requireNonNull(keyPairRequest.getBody()).get("random")
                    );
            Map<String, byte[]> keyPairResponse = new HashMap<>();
            keyPairResponse.put("public", keyPair.getPublic().getEncoded());
            keyPairResponse.put("private", keyPair.getPrivate().getEncoded());
            return new ResponseEntity<>(keyPairResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new RuntimeException(String.format("User: %s is disabled", username), e);
        } catch (BadCredentialsException e) {
            throw new RuntimeException(String.format("Invalid credentials for user: %s", username), e);
        }
    }
}
