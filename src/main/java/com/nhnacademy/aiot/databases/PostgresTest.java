package com.nhnacademy.aiot.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresTest {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/xflow";
        String user = "postgres";
        String password = "root";
        String sql = "create table sensorInfo (\n" + //
                "  id int primary key generated always as identity,\n" + //
                "  type varchar(15) not null,\n" + //
                "  deviceEui varchar(40) not null,\n" + //
                "  site varchar(15) not null default 'nhnacademy',\n" + //
                "  branch varchar(15) not null,\n" + //
                "  place varchar(15) not null ,\n" + //
                "  unitId int not null,\n" + //
                "  Address int not null\n" + //
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
