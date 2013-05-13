package com.rethinkdb;

public abstract class Datum extends Query {
    public double toNum() throws Exception {
        throw new Exception("Not a num");
    }

    public String toStr() throws Exception {
        throw new Exception("Not a str");
    }

    protected static Datum wrap(Ql2.Datum datum) throws Exception {
        switch (datum.getType()) {
        case R_NUM:
            return new NumDatum(datum.getRNum());
        case R_STR:
            return new StrDatum(datum.getRStr());
        default:
            throw new Exception("unexpected datum type");
        }
    }
}
