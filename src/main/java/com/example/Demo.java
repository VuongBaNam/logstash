package com.example;

import com.google.gson.Gson;
import com.sun.jmx.snmp.Timestamp;


/**
 * Nam on 10/11/2018
 */
public class Demo {
    public static void main(String[] args) {
        Item item = new Item();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        item.setAttribute("a","a");
        item.setAttribute("b",timestamp);

        Gson gson = new Gson();

        System.out.println(gson.toJson(item.getFields()));
    }
}
