package com.rethinkdb;

public class Test {

    // Test the RethinkDB driver
	public static void main(String[] args) throws Exception {
        Connection c = new Connection();

        Query q = Query.expr(23);
        double val = q.run(c).toNum();
        System.out.println(val);
	}

}
