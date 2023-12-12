package com.nhnacademy.aiot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


public class ReadPostgres {
    static JSONArray jsonArrays;
    static List<JSONObject> jsonList;

    public static List<JSONObject> getJsonArray() {
        jsonList = new ArrayList<>();
        String url = "jdbc:postgresql://localhost:5432/xflow";
        String user = "postgres";
        String password = "root";
        String sql = "select * from sensorinfo";
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
                    jsonList.add(json);
                }

            } else {
                throw new SQLException("no connection...");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());

        }
        return jsonList;
    }
}
