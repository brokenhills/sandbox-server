package com.brokenhills.sandboxserver.service;

import com.brokenhills.sandboxserver.model.UserRequest;
import com.brokenhills.sandboxserver.model.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<Void> createUser(RequestEntity<UserRequest> credentials) {
        try {
            userService.createUser(Objects.requireNonNull(credentials.getBody()).getLogin(),
                    credentials.getBody().getPassword(), UserStatus.ACTIVE);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating user!", e);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
