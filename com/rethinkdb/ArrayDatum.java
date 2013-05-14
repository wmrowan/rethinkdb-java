package com.rethinkdb;

import java.util.List;
import java.util.ArrayList;

public class ArrayDatum extends Datum {
    public ArrayDatum(List<Ql2.Datum> array) throws Exception {
        this.array = new ArrayList<Datum>();
        for (Ql2.Datum datum : array) {
            this.array.add(Datum.wrap(datum));
        }
    }

    public List<Datum> toArray() {
        return array;
    }

    protected Ql2.Term build() {
        Ql2.Term.Builder term = Ql2.Term.newBuilder()
            .setType(Ql2.Term.TermType.MAKE_ARRAY);

        for (Datum datum : array) {
            term.addArgs(datum.build());
        }

        return term.build();
    }

    private List<Datum> array;
}
