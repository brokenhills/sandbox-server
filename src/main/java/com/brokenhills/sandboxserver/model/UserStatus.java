package com.brokenhills.sandboxserver.model;

public enum UserStatus {

    ADMIN("admin"),
    USER("user");

    private String value;

    UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
