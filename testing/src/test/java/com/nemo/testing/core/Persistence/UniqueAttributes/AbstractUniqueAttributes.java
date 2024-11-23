package com.nemo.testing.core.Persistence.UniqueAttributes;

import org.jooq.Condition;

/**
 * Attributes to be able to identify an entry in the database.
 * */
public abstract class AbstractUniqueAttributes {

    protected Integer id;

    protected AbstractUniqueAttributes() {
        this.id = null;
    }

    protected AbstractUniqueAttributes(Integer id) {
        this.id = id;
    }

    /**
     * Whether provided attributes are enough to assert uniqueness
     * */
    public abstract boolean enoughAttributesProvided();

    /**
     * Builds the WHERE clause for a SELECT ... FROM database query
     * */
    public abstract Condition buildWhereClause();

    public boolean idProvided() {
        return id != null;
    }

}
