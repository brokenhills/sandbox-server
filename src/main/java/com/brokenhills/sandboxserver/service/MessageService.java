package com.brokenhills.sandboxserver.service;

import com.brokenhills.sandboxserver.model.Message;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageService {

    private final MongoTemplate mongoTemplate;

    public MessageService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void createMessage(String author, String message) {
        Message newMessage = new Message();
        newMessage.setAuthor(author);
        newMessage.setMessage(message);
        mongoTemplate.save(newMessage);
    }

    public List<Message> getMessagesByAuthor(String author) {
        Query query = new Query();
        query.addCriteria(Criteria.where("author").is(author));
        return mongoTemplate.find(query, Message.class);
    }
}
