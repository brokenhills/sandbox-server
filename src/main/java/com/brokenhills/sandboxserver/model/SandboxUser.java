package com.brokenhills.sandboxserver.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SandboxUser {
    @Id
    @Generated
    private ObjectId _id;

    private String login;

    private String password;

    @CreatedDate
    private Instant dateCreated;

    @LastModifiedDate
    private Instant dateModified;

    private UserStatus status;
}
