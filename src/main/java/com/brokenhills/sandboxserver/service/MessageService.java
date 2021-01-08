package com.brokenhills.sandboxserver.service;

import com.brokenhills.sandboxserver.model.Message;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageService {

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Message> kafkaTemplate;
    private final String kafkaTopic;


    public MessageService(MongoTemplate mongoTemplate,
                          KafkaTemplate<String, Message> kafkaTemplate,
                          String kafkaTopic) {
        this.mongoTemplate = mongoTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopic = kafkaTopic;
    }

    public void createMessage(String author, String message) {
        Message newMessage = new Message();
        newMessage.setAuthor(author);
        newMessage.setMessage(message);
        kafkaTemplate.send(kafkaTopic, newMessage);
        mongoTemplate.save(newMessage);
    }

    public List<Message> getMessagesByAuthor(String author) {
        Query query = new Query();
        query.addCriteria(Criteria.where("author").is(author));
        return mongoTemplate.find(query, Message.class);
    }

    public Message getMessageById(String id) {
        return mongoTemplate.findById(id, Message.class);
    }
}
