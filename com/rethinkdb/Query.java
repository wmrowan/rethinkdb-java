package com.rethinkdb;

import java.util.List;

public abstract class Query {
    public Query() { }

    public <T> T run(Connection conn) throws Exception {
        Ql2.Query query = Ql2.Query.newBuilder()
            .setType(Ql2.Query.QueryType.START)
            .setToken(1)
            .setQuery(this.build()).build();

        return conn.send(query);
    }

    protected abstract Ql2.Term build();

    public static <T> Query expr(T value) throws Exception {
        return Datum.wrap(value);
    }
}
