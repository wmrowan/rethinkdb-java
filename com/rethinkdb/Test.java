package com.rethinkdb;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Test {

    // Test the RethinkDB driver
	public static void main(String[] args) throws Exception {
        Connection c = new Connection();

        Map<String, String> map = new HashMap<String, String>();
        map.put("a", "b");
        map.put("c", "d");

        Query q = Query.expr(map);
        Map<String, String> out = q.run(c);

        //Query q = Query.expr("abc");
        //String out = q.run(c);
        System.out.println(out);
	}

}
