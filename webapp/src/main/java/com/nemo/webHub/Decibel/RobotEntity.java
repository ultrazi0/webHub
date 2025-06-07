package com.nemo.webHub.Decibel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nemo.webHub.User.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jooq.generated.tables.records.RobotsRecord;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RobotEntity {

    private final int id;
    @Setter
    @NotNull
    private String name;
    @Setter
    @JsonIgnore
    private String password;
    private final OffsetDateTime createdAt;
    private final User owner;
    @ToString.Exclude
    private boolean isOnline = false;
    @NotNull
    private final Set<User> sharedUsers = new HashSet<>();

    public RobotEntity(int id, String name, UUID password, OffsetDateTime createdAt, int ownerId) {
        this(id, name, "{noop}" + password, createdAt, ownerId);
    }

    public RobotEntity(int id, String name, String password, OffsetDateTime createdAt, int ownerId) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.createdAt = createdAt;
        this.owner = new User(ownerId, null);
    }

    public RobotEntity(RobotsRecord robotsRecord) {
        this.id = robotsRecord.getRobotId();
        this.name = robotsRecord.getName();
        this.password = "{noop}" + robotsRecord.getPassword();
        this.createdAt = robotsRecord.getCreatedAt();
        this.owner = new User(robotsRecord.getOwnerId(), null);
    }

    public static RobotEntity of(RobotsRecord robotsRecord) {
        return new RobotEntity(robotsRecord);
    }

    public RobotEntity withOwnerName(String ownerName) {
        this.owner.setUsername(ownerName);
        return this;
    }

    public RobotEntity withIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
        return this;
    }

    public RobotEntity withSharedUsers(Collection<User> sharedUsers) {
        this.sharedUsers.addAll(sharedUsers);
        return this;
    }

    /**
     * This method is needed to put a password without the encoding in a serialized JSON
     */
    @JsonProperty("password")
    public String getPasswordWithoutEncoding() {
        Pattern pattern = Pattern.compile("^(?<encryption>\\{[a-z]*})(?<password>\\S*)$");
        Matcher matcher = pattern.matcher(password);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Provided password does not match the regular expression");
        }
        return matcher.group("password");
    }
}
