package com.nhnacademy.aiot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class ReadPostgres {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/xflow";
        String user = "postgres";
        String password = "root";
        String sql = "select * from sensorinfo";

        try (Connection connect = DriverManager.getConnection(url, user, password);
                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (!connect.isClosed()) {
                while (rs.next()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        System.out.print(rs.getObject(i) + " | ");
                    }
                    System.out.println("\n");
                }

            } else {
                throw new SQLException("no connection...");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());

        }

    }
}
