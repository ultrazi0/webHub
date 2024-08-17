package com.nemo.webHub.Decibel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.jooq.generated.tables.records.RobotsRecord;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RobotEntity {
    private final int id;  // int cannot be null
    @NotNull
    private String name;
    @JsonIgnore
    private String password;
    private final OffsetDateTime createdAt;
    @NotNull
    private final int ownerId;
    private String ownerName = null;

    public RobotEntity(int id, String name, UUID password, OffsetDateTime createdAt, int ownerId) {
        this.id = id;
        this.name = name;
        this.password = "{noop}" + password;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
    }

    public RobotEntity(int id, String name, String password, OffsetDateTime createdAt, int ownerId) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
    }

    public RobotEntity(RobotsRecord robotsRecord) {
        this.id = robotsRecord.getRobotId();
        this.name = robotsRecord.getName();
        this.password = "{noop}" + robotsRecord.getPassword();
        this.createdAt = robotsRecord.getCreatedAt();
        this.ownerId = robotsRecord.getOwnerId();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public RobotEntity setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    @Override
    public String toString() {
        return "RobotEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password=" + password +
                ", createdAt=" + createdAt +
                ", ownerId=" + ownerId +
                ", ownerName=" + ownerName +
                '}';
    }
}
