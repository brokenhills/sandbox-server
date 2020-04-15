package com.brokenhills.sandboxserver.service;

import com.brokenhills.sandboxserver.model.SandboxUser;
import com.brokenhills.sandboxserver.model.UserStatus;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        SandboxUser user = getUserByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with login: %s was not found!", login));
        }
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("user"));
        return new User(user.getLogin(), user.getPassword(), authorities);
    }

    public void createUser(String login, String password, UserStatus status) {
        SandboxUser newUser = new SandboxUser();
        newUser.setLogin(login);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setStatus(status);
        mongoTemplate.save(newUser);
    }

    public SandboxUser getUserByLogin(String login) {
        Query query = new Query();
        query.addCriteria(Criteria.where("login").is(login));
        List<SandboxUser> searchedUser = mongoTemplate.find(query, SandboxUser.class);
        if (searchedUser.size() > 1) {
            throw new RuntimeException(String.format("More than one user with login: %s were found!", login));
        }
        return searchedUser.get(0);
    }

    public List<SandboxUser> getAllUsers() {
        return mongoTemplate.findAll(SandboxUser.class);
    }

    public void deleteUser(String login) {
        mongoTemplate.remove(getUserByLogin(login));
    }

}
