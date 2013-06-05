package com.rethinkdb;

import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class OpTerm extends Term {

    public OpTerm(Ql2.Term.TermType termtype, Object... args) throws Exception {
        this.termtype = termtype;
        this.args = new ArrayList<Term>();
        this.optargs = new HashMap<String, Term>();
        this.apply(args);
    }

    public <T> OpTerm apply(T... args) throws Exception {
        return this.apply(Arrays.asList(args));
    }

    public <T> OpTerm apply(Collection<T> args) throws Exception {
        if (args != null) for (Object obj : args) {
            this.args.add(Term.expr(obj));
        }

        return this;
    }

    public <T> OpTerm addOption(String opt, T val) throws Exception {
        this.optargs.put(opt, Datum.wrap(val));
        return this;
    }

    protected Ql2.Term build() {
        Ql2.Term.Builder term = Ql2.Term.newBuilder().setType(termtype);

        for (Term t : args) {
            term.addArgs(t.build());
        }

        for (Map.Entry<String, Term> entry : optargs.entrySet()) {
            term.addOptargs(
                Ql2.Term.AssocPair.newBuilder()
                    .setKey(entry.getKey())
                    .setVal(entry.getValue().build())
                );
        }

        return term.build();
    }

    private Ql2.Term.TermType termtype;
    private List<Term> args;
    private Map<String, Term> optargs;
}
