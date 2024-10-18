package com.nemo.webHub.Decibel;

import jakarta.annotation.PostConstruct;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static org.jooq.generated.Tables.ROBOTS;


@Component
public class DBConfig {

    private final DSLContext dslContext;

    @Autowired
    public DBConfig(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @PostConstruct
    private void init() {
        /*
         * This method starts JOOQ initialization
         * It seems that Spring Boot does not initialize JDBC before the first request is made.
         * Not like this is mandatory or not starting on startup is a problem - its just messy,
         * so it's better to keep it at the top
         */

        Result<Record> result = dslContext.select().from(ROBOTS).fetch();

        for (Record r : result) {
            Integer id = r.getValue(ROBOTS.ROBOT_ID);
            String name = r.getValue(ROBOTS.NAME);
            OffsetDateTime createdAt = r.getValue(ROBOTS.CREATED_AT);

            System.out.println("ID: " + id + "; name: " + name + "; created at: " + createdAt);
        }

    }
}
