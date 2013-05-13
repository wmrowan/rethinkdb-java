package com.rethinkdb;

public class NumDatum extends Datum {
    public NumDatum(double num) {
        this.num = num;
    }

    public double toNum() {
        return num;
    }

    protected Ql2.Term build() {
        return Ql2.Term.newBuilder()
                .setType(Ql2.Term.TermType.DATUM)
                .setDatum(
                    Ql2.Datum.newBuilder()
                        .setType(Ql2.Datum.DatumType.R_NUM)
                        .setRNum(this.num)
                ).build();
    }

    private double num;
}
