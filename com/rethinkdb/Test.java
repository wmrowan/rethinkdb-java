package com.rethinkdb;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class Test {

    // Test the RethinkDB driver
	public static void main(String[] args) throws Exception {
        Connection c = new Connection();

        String table = "usertable";
        String key = "a";
        Set<String> fields = null;//new HashSet<String>();
        //fields.add("b");
        Term t = Term.table(table).get(key).pluck(fields);
        Map<String, String> out = t.run(c);

        System.out.println(out);
	}

}
