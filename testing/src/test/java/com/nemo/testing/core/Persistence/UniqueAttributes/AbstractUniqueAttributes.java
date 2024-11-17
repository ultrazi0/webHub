package com.nemo.testing.core.Persistence.UniqueAttributes;

import org.jooq.Condition;

public abstract class AbstractUniqueAttributes {

    protected Integer id;

    protected AbstractUniqueAttributes() {
        this.id = null;
    }

    protected AbstractUniqueAttributes(Integer id) {
        this.id = id;
    }

    public abstract boolean enoughAttributesProvided();

    public abstract Condition buildWhereClause();

    public boolean idProvided() {
        return id != null;
    }

}
