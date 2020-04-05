package com.brokenhills.sandboxserver.service;

import com.brokenhills.sandboxserver.model.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Objects;

@RestController
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/message")
    public ResponseEntity<Void> createMessage(RequestEntity<Message> message) {
        try {
            messageService.createMessage(Objects.requireNonNull(message.getBody()).getAuthor(),
                    message.getBody().getMessage());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Error creating message! %s", e));
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/message/{author}")
    public ResponseEntity<List<Message>> getMessageByAuthor(@PathVariable("author") String author) {
        List<Message> messages;
        try {
            messages = messageService.getMessagesByAuthor(author);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Error creating message! %s", e));
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
