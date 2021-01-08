package com.brokenhills.sandboxserver.service;

import com.brokenhills.sandboxserver.model.Message;
import com.brokenhills.sandboxserver.model.MessageRequest;
import com.brokenhills.sandboxserver.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
public class MessageController {

    private final MessageService messageService;
    private final TokenService tokenService;

    public MessageController(MessageService messageService,
                             TokenService tokenService) {
        this.messageService = messageService;
        this.tokenService = tokenService;
    }

    @PostMapping("/message")
    public ResponseEntity<Void> createMessage(RequestEntity<MessageRequest> message) {
        try {
            messageService.createMessage(getAuthorFromRequest(message),
                    Objects.requireNonNull(message.getBody()).getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Error creating message! %s", e.getMessage()));
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/message")
    public ResponseEntity<List<Message>> getMessageList(RequestEntity<?> requestEntity) {
        List<Message> messages;
        try {
            messages = messageService.getMessagesByAuthor(getAuthorFromRequest(requestEntity));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Error getting messages! %s", e.getMessage()));
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/message/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("id") String messageId) {
        Message message = messageService.getMessageById(messageId);
        if (message == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Message with id: %s wasn't found", messageId));
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    private String getAuthorFromRequest(RequestEntity<?> requestEntity) {
        String authHeaderValue = requestEntity.getHeaders().getFirst("Authorization");
        String token = tokenService.extractToken(Objects.requireNonNull(authHeaderValue));
        return tokenService.getUsernameFromToken(token);
    }
}
