package com.rethinkdb;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

abstract class Datum extends Term {

    @SuppressWarnings("unchecked")
    protected abstract <T, S> T unwrap() throws Exception;

    @SuppressWarnings("unchecked")
    protected static <T> Datum wrap(T value) throws Exception {
        if (value == null) {
            return new NullDatum();
        } else if (value instanceof Boolean) {
            return new BoolDatum((Boolean)value);
        } else if (value instanceof Double) {
            return new NumDatum((Double)value);
        } else if (value instanceof Integer) {
            return new NumDatum((Double)(((Integer)value) * 1.0));
        } else if (value instanceof String) {
            return new StrDatum((String)value);
        } else if (value instanceof List) {
            return new ArrayDatum((List)value);
        } else if (value instanceof Map) {
            return new ObjectDatum((Map)value);
        } else {
            throw new Exception("Cannot convert " + value + " to datum");
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> T unwrapProto(Ql2.Datum datum) throws Exception {
        switch (datum.getType()) {
        case R_NULL:
            return null;
        case R_BOOL:
             return (T)(Boolean)datum.getRBool();
        case R_NUM:
            return (T)(Double)datum.getRNum();
        case R_STR:
            return (T)datum.getRStr();
        case R_ARRAY:
            return (T)ArrayDatum.unwrapProto(datum.getRArrayList());
        case R_OBJECT:
            return ObjectDatum.unwrapProto(datum.getRObjectList());
        default:
            throw new Exception("unexpected datum type");
        }
    }

    static class NullDatum extends Datum {
        @SuppressWarnings("unchecked")
        public <T, S> T unwrap() throws Exception {
            return null;
        }

        protected Ql2.Term build() {
            return Ql2.Term.newBuilder()
                .setType(Ql2.Term.TermType.DATUM)
                .setDatum(
                    Ql2.Datum.newBuilder()
                        .setType(Ql2.Datum.DatumType.R_NULL)
                ).build();
        }
    }

    static class BoolDatum extends Datum {
        public BoolDatum(boolean bool) {
            this.bool = bool;
        }

        @SuppressWarnings("unchecked")
        public <T, S> T unwrap() throws Exception {
            return (T)(Boolean)bool;
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

    static class NumDatum extends Datum {
        public NumDatum(double num) {
            this.num = num;
        }

        @SuppressWarnings("unchecked")
        public <T, S> T unwrap() throws Exception {
            return (T)(Double)num;
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

    static class StrDatum extends Datum {
        public StrDatum(String str) {
            this.str = str;
        }

        @SuppressWarnings("unchecked")
        public <T, S> T unwrap() throws Exception {
            return (T)str;
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

    static class ArrayDatum extends Datum {

        public <T> ArrayDatum(List<T> array) throws Exception {
            this.array = new ArrayList<Datum>();
            for (T val : array) {
                this.array.add(Datum.wrap(val));
            }
        }

        @SuppressWarnings("unchecked")
        public static <T, S> T unwrapProto(List<Ql2.Datum> array) throws Exception {
            ArrayList<S> arr = new ArrayList();
            for (Ql2.Datum datum : array) {
                arr.add((S)Datum.unwrapProto(datum));
            }

            return (T)arr;
        }

        @SuppressWarnings("unchecked")
        public <T, S> T unwrap() throws Exception {
            ArrayList<S> arr = new ArrayList();
            for (Datum datum : array) {
                arr.add((S)datum.unwrap());
            }

            return (T)arr;
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

    static class ObjectDatum extends Datum {

        public <T> ObjectDatum(Map<String,T> object) throws Exception {
            this.object = new HashMap<String, Datum>();
            for (Map.Entry<String, T> entry : object.entrySet()) {
                this.object.put(entry.getKey(), Datum.wrap(entry.getValue()));
            }
        }

        @SuppressWarnings("unchecked")
        public static <T, S> T unwrapProto(List<Ql2.Datum.AssocPair> object) throws Exception {
            Map<String, S> obj = new HashMap<String, S>();
            for (Ql2.Datum.AssocPair pair : object) {
                obj.put(pair.getKey(), (S)Datum.unwrapProto(pair.getVal()));
            }

            return (T)obj;
        }

        @SuppressWarnings("unchecked")
        public <T, S> T unwrap() throws Exception {
            Map<String, S> obj = new HashMap<String, S>();
            for (Map.Entry<String,Datum> entry : object.entrySet()) {
                obj.put(entry.getKey(), (S)entry.getValue().unwrap());
            }

            return (T)obj;
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

        private Map<String, Datum> object;
    }
}
