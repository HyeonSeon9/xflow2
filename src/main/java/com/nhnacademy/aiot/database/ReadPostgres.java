package com.nhnacademy.aiot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ReadPostgres {

    public static JSONArray getJsonArray() {
        String url = "jdbc:postgresql://localhost:5432/xflow";
        String user = "postgres";
        String password = "root";
        String sql = "select * from sensorinfo";
        JSONArray jsonArray = new JSONArray();
        try (Connection connect = DriverManager.getConnection(url, user, password);
                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (!connect.isClosed()) {
                while (rs.next()) {
                    JSONObject json = new JSONObject();
                    ResultSetMetaData metaData = rs.getMetaData();
                    json.put("id", rs.getString("id"));
                    JSONObject payload = new JSONObject();
                    for (int i = 2; i <= metaData.getColumnCount(); i++) {
                        json.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    jsonArray.add(json);
                }

            } else {
                throw new SQLException("no connection...");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());

        }

        return jsonArray;
    }
}
