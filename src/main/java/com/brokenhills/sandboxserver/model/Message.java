package com.brokenhills.sandboxserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @Generated
    private ObjectId _id;

    private String author;

    private String message;

    @CreatedDate
    private Instant dateCreated;

    @LastModifiedDate
    private Instant dateModified;
}
