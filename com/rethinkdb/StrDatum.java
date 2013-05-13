package com.rethinkdb;

public class StrDatum extends Datum {
    public StrDatum(String str) {
        this.str = str;
    }

    public String toStr() {
        return str;
    }

    protected Ql2.Term build() {
        return Ql2.Term.newBuilder()
            .setType(Ql2.Term.TermType.DATUM)
            .setDatum(
                Ql2.Datum.newBuilder()
                    .setType(Ql2.Datum.DatumType.R_STR)
                    .setRStr(this.str)
            ).build();
    }

    private String str;
}
