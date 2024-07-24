package com.nemo.webHub.Decibel;

import jakarta.annotation.PostConstruct;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.jooq.generated.Tables.*;


@Component
public class DBConfig {

    @Autowired
    private DSLContext dslContext;

    @PostConstruct
    private void test() {
        // This method starts JOOQ initialization
        // It seems that Spring Boot does not initialize JDBC before the first request is made

        Result<Record> result = dslContext
                .select()
                .from(ROBOTS).fetch();

        for (Record r : result) {
            Integer id = r.getValue(ROBOTS.ID);
            String name = r.getValue(ROBOTS.NAME);
            LocalDateTime createdAt = r.getValue(ROBOTS.CREATED_AT);

            System.out.println("ID: " + id + "; name: " + name + "; created at: " + createdAt);
        }

    }
}
