package com.nemo.webHub.Decibel;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.jooq.generated.tables.records.RobotsRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RobotEntity {
    private final int id;  // int cannot be null
    @NotNull
    private String name;
    private final OffsetDateTime createdAt;
    @NotNull
    private final int ownerId;
    private String ownerName = null;

    public RobotEntity(int id, String name, OffsetDateTime createdAt, int ownerId) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
    }

    public RobotEntity(RobotsRecord robotsRecord) {
        this.id = robotsRecord.getRobotId();
        this.name = robotsRecord.getName();
        this.createdAt = robotsRecord.getCreatedAt();
        this.ownerId = robotsRecord.getOwnerId();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

    public RobotEntity setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        return this;
    }

    @Override
    public String toString() {
        return "RobotEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", ownerId=" + ownerId +
                '}';
    }
}
