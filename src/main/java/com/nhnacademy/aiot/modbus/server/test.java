package com.nhnacademy.aiot.modbus.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONObject;

public class test {
    public static void main(String[] args) throws IOException {

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
                    JSONObject payload = new JSONObject();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        json.put(metaData.getColumnName(i), rs.getObject(i));
                        // System.out.print(metaData.getColumnName(i) + " " + rs.getObject(i));
                    }
                    System.out.println(json);
                }
            } else {
                throw new SQLException("no connection...");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());

        }

    }
}
