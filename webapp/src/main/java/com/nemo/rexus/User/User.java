package com.nemo.rexus.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class User {

    private final int id;
    private String username;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User other)) {
            return false;
        }
        return super.equals(other) || getId() == other.getId();
    }
}
