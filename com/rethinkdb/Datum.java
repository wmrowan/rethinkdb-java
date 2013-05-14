package com.rethinkdb;

import java.util.List;
import java.util.Map;

public abstract class Datum extends Query {
    public boolean toBool() throws Exception {
        throw new Exception("Not a bool");
    }

    public double toNum() throws Exception {
        throw new Exception("Not a num");
    }

    public String toStr() throws Exception {
        throw new Exception("Not a str");
    }

    public List<Datum> toArray() throws Exception {
        throw new Exception("Not an array");
    }

    public Map<String,Datum> toObject() throws Exception {
        throw new Exception("Not an object");
    }

    protected static Datum wrap(Ql2.Datum datum) throws Exception {
        switch (datum.getType()) {
        case R_NULL:
            return null;
        case R_BOOL:
            return new BoolDatum(datum.getRBool());
        case R_NUM:
            return new NumDatum(datum.getRNum());
        case R_STR:
            return new StrDatum(datum.getRStr());
        case R_ARRAY:
            return new ArrayDatum(datum.getRArrayList());
        case R_OBJECT:
            return new ObjectDatum(datum.getRObjectList());
        default:
            throw new Exception("unexpected datum type");
        }
    }
}
