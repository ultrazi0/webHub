package com.nemo.rexus.Decibel;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.nemo.rexus.User.User;
import lombok.Getter;
import lombok.Setter;
import org.jooq.generated.tables.records.UsersRecord;

import java.time.OffsetDateTime;

@Getter
@JsonIncludeProperties({"id", "username"})
public class UserEntity extends User {

    @Setter
    private String password;
    private final String[] roles;
    private final OffsetDateTime createdAt;

    public UserEntity(int id, String username, String password, String[] roles, OffsetDateTime createdAt) {
        super(id, username);
        this.password = password;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public UserEntity(UsersRecord usersRecord) {
        super(usersRecord.getUserId(), usersRecord.getUsername());
        this.password = usersRecord.getPassword();
        this.roles = usersRecord.getRoles();
        this.createdAt = usersRecord.getCreatedAt();
    }

    public static UserEntity of(UsersRecord userRecord) {
        return new UserEntity(userRecord);
    }

    public void updateUser(UserEntity referenceUser) {
        setUsername(referenceUser.getUsername());
    }
}
