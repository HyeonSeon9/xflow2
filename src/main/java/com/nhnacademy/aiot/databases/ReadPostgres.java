package com.nhnacademy.aiot.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadPostgres {
    public static void main(String[] args) {
        JSONObject jsonObject;
        List<JSONObject> jsonList = new ArrayList<>();
        String url = "jdbc:postgresql://localhost:5432/xflow";
        String user = "postgres";
        String password = "root";
        String sql = "select * from sensorinfo";

        try (Connection connection = DriverManager.getConnection(url, user, password);
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);) {

            if (!connection.isClosed()) {
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
            }

        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        System.out.println(jsonList);


    }
}
