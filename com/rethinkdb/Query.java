package com.rethinkdb;

public abstract class Query {
    public Query() { }

    public Datum run(Connection conn) throws Exception {
        Ql2.Query query = Ql2.Query.newBuilder()
            .setType(Ql2.Query.QueryType.START)
            .setToken(1)
            .setQuery(this.build()).build();

        return conn.send(query);
    }

    protected abstract Ql2.Term build();

    public static Query expr(double num) {
       return new NumDatum(num);
    }

    public static Query expr(String str) {
        return new StrDatum(str);
    }
}
