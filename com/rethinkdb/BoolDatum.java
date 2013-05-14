package com.rethinkdb;

public class BoolDatum extends Datum {
    public BoolDatum(boolean bool) {
        this.bool = bool;
    }

    public boolean toBool() {
        return bool;
    }

    protected Ql2.Term build() {
        return Ql2.Term.newBuilder()
            .setType(Ql2.Term.TermType.DATUM)
            .setDatum(
                Ql2.Datum.newBuilder()
                    .setType(Ql2.Datum.DatumType.R_BOOL)
                    .setRBool(this.bool)
            ).build();
    }

    private boolean bool;
}
