package com.nemo.webHub.Decibel;

import jakarta.validation.constraints.NotNull;
import org.jooq.generated.tables.records.RobotsRecord;

import java.time.LocalDateTime;

public class RobotEntity {
    private final int id;  // int cannot be null
    @NotNull
    private String name;
    private final LocalDateTime createdAt;

    public RobotEntity(int id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public RobotEntity(RobotsRecord robotsRecord) {
        this.id = robotsRecord.getId();
        this.name = robotsRecord.getName();
        this.createdAt = robotsRecord.getCreatedAt();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RobotEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
