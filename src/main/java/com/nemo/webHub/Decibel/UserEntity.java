package com.nemo.webHub.Decibel;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import org.jooq.generated.tables.records.UsersRecord;

import java.time.OffsetDateTime;

@JsonIncludeProperties({"id", "username"})
public class UserEntity {

    private final int id;
    private String username;
    private String password;
    private final String[] roles;
    private final OffsetDateTime createdAt;

    public UserEntity(int id, String username, String password, String[] roles, OffsetDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public UserEntity(UsersRecord usersRecord) {
        this.id = usersRecord.getUserId();
        this.username = usersRecord.getUsername();
        this.password = usersRecord.getPassword();
        this.roles = usersRecord.getRoles();
        this.createdAt = usersRecord.getCreatedAt();
    }

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String[] getRoles() {
        return roles;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void updateUser(UserEntity referenceUser) {
        this.username = referenceUser.getUsername();
    }
}
