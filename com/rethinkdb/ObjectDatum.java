package com.rethinkdb;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ObjectDatum extends Datum {
    public ObjectDatum(List<Ql2.Datum.AssocPair> object) throws Exception {
        this.object = new HashMap<String,Datum>();
        for (Ql2.Datum.AssocPair pair : object) {
            this.object.put(pair.getKey(), Datum.wrap(pair.getVal()));
        }
    }

    public Map<String,Datum> toObject() {
        return object;
    }

    protected Ql2.Term build() {
        Ql2.Term.Builder term = Ql2.Term.newBuilder()
            .setType(Ql2.Term.TermType.MAKE_OBJ);

        for (Map.Entry<String,Datum> entry : object.entrySet()) {
            Ql2.Term.AssocPair.Builder pair = Ql2.Term.AssocPair.newBuilder()
                                              .setKey(entry.getKey())
                                              .setVal(entry.getValue().build());
            term.addOptargs(pair.build());
        }

        return term.build();
    }

    private Map<String,Datum> object;
}
