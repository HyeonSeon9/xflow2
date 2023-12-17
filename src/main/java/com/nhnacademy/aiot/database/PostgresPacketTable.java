package com.nhnacademy.aiot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresPacketTable {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/xflow";
        String user = "postgres";
        String password = "root";
        String sql = "create table packetInfo (\n" + //
                "  id int primary key generated always as identity,\n" + //
                "  send int not null,\n" + //
                "  receive int not null,\n" + //
                "  startTime varchar(30) not null,\n" + //
                "  endTime varchar(30) not null\n" + //
                ");";

        try (Connection connect = DriverManager.getConnection(url, user, password);
                Statement stmt = connect.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (!connect.isClosed()) {
                while (rs.next()) {
                    System.out.println(rs.getInt("id"));
                    System.out.println(rs.getString("name"));
                }

            } else {
                throw new SQLException("no connection...");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());

        }

    }
}
