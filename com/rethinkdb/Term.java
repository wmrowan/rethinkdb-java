package com.rethinkdb;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class Term {

    public <T> T run(Connection conn) throws Exception {
        return conn.run(this);
    }

    public void run_noreply(Connection conn) throws Exception {
        conn.run_noreply(this);
    }

    protected abstract Ql2.Term build();

    // Static top level functions

    public static <T> Term expr(T value) throws Exception {
        if (value instanceof Term) {
            return (Term)value;
        } else {
            return Datum.wrap(value);
        }
    }

    public static OpTerm db(String name) throws Exception {
        return new OpTerm(Ql2.Term.TermType.DB, name);
    }

    public static OpTerm table(String name) throws Exception {
        return new OpTerm(Ql2.Term.TermType.TABLE, name);
    }

    public static OpTerm db_list() throws Exception {
        return new OpTerm(Ql2.Term.TermType.DB_LIST);
    }

    public static OpTerm db_create(String name) throws Exception {
        return new OpTerm(Ql2.Term.TermType.DB_CREATE, name);
    }

    // List tables on the default database
    public static OpTerm table_list() throws Exception {
        return new OpTerm(Ql2.Term.TermType.TABLE_LIST);
    }

    // Create table on the default database
    public static OpTerm table_create(String name) throws Exception {
        return new OpTerm(Ql2.Term.TermType.TABLE_CREATE, name);
    }

    // Methods for chaining

    public <T> OpTerm get(T key) throws Exception {
        return new OpTerm(Ql2.Term.TermType.GET, this, key);
    }

    public OpTerm pluck(String... fields) throws Exception {
        return (new OpTerm(Ql2.Term.TermType.PLUCK, this)).apply(fields);
    }

    public OpTerm pluck(Collection<String> fields) throws Exception {
        return (new OpTerm(Ql2.Term.TermType.PLUCK, this)).apply(fields);
    }

    public OpTerm limit(int limit) throws Exception {
        return new OpTerm(Ql2.Term.TermType.LIMIT, this, limit);
    }

    public <T> OpTerm between(T lowerBound, T upperBound) throws Exception {
        return new OpTerm(Ql2.Term.TermType.BETWEEN, this, lowerBound, upperBound);
    }

    public <T> OpTerm update(Map<String, T> obj) throws Exception {
        return new OpTerm(Ql2.Term.TermType.UPDATE, this, obj);
    }

    public <T> OpTerm insert(Map<String, T> obj) throws Exception {
        return new OpTerm(Ql2.Term.TermType.INSERT, this, obj);
    }

    public OpTerm delete() throws Exception {
        return new OpTerm(Ql2.Term.TermType.DELETE, this);
    }
}
