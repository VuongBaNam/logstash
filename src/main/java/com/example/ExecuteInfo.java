package com.example;

import com.google.gson.Gson;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Nam on 10/11/2018
 */

public class ExecuteInfo implements Runnable {
    private Connection connection;
    private Statement statement;
    private String sql;
    private RestTemplate restTemplate;
    private String index;
    private Gson gson;

    public static final String url = "localhost:9200/_bulk";

    public ExecuteInfo(String sql,String index) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        this.connection  = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/demo","root","admin");
        this.statement = connection.createStatement();
        this.sql = sql;
        this.index = index;
        restTemplate = new RestTemplate();
        gson = new Gson();
    }

    public void run() {
        try {
            runSqlStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void runSqlStatement(String sql) throws SQLException {
        ResultSet rs = statement.executeQuery(sql);
        ResultSetMetaData metaData = rs.getMetaData();
        int numberOfColumn = metaData.getColumnCount();
        while(rs.next()){
            Item item = new Item();
            for(int i = 1;i <= numberOfColumn;i++){

                Object data = rs.getObject(i);
                String s = data.getClass().getName();
                if (s.equals("java.sql.Timestamp")){
                    item.setAttribute(metaData.getColumnName(i),toDateElasticsearch(rs.getString(i)));
                }else
                    item.setAttribute(metaData.getColumnName(i),data);
            }
            String data = gson.toJson(item);
            restTemplate.postForObject(url,data,String.class);
        }
    }

    public String toDateElasticsearch(String date){

        String a[] = date.split(" ");
        return a[0]+"T"+a[1]+".000Z";

    }
}
