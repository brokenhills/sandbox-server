package com.brokenhills.sandboxserver.service;

import com.brokenhills.sandboxserver.model.SandboxUser;
import com.brokenhills.sandboxserver.model.UserStatus;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@EnableMongoAuditing
public class UserService implements UserDetailsService {

    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SandboxUser user = getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with username: %s was not found!", username));
        }
        return user;
    }

    public void createUser(String username, String password, UserStatus status) {
        SandboxUser newUser = new SandboxUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority(status.getValue())));
        mongoTemplate.save(newUser);
    }

    public SandboxUser getUserByUsername(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        List<SandboxUser> searchedUser = mongoTemplate.find(query, SandboxUser.class);
        if (searchedUser.size() > 1) {
            throw new RuntimeException(String.format("More than one user with username: %s were found!", username));
        }
        return searchedUser.get(0);
    }

    public List<SandboxUser> getAllUsers() {
        return mongoTemplate.findAll(SandboxUser.class);
    }

    public void deleteUser(String username) {
        mongoTemplate.remove(getUserByUsername(username));
    }

}
