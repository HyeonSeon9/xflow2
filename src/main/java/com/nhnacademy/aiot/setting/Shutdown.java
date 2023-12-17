package com.nhnacademy.aiot.setting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.nhnacademy.aiot.node.RuleEngineNode;

public class Shutdown implements Runnable {
    String startTime;

    public Shutdown() {
        LocalDateTime start = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        this.startTime = start.format(formatter);
    }

    public void run() {
        String url = "jdbc:postgresql://localhost:5432/xflow";
        String user = "postgres";
        String password = "root";
        String sql =
                "INSERT INTO packetinfo (id, send,receive, starttime, endtime) VALUES (default,?,?,?,?)";
        LocalDateTime end = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String endTime = end.format(formatter);
        try (Connection connect = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = connect.prepareStatement(sql);) {

            if (!connect.isClosed()) {
                pstmt.setInt(1, RuleEngineNode.getMessageCount());
                pstmt.setInt(2, 0);
                pstmt.setString(3, startTime);
                pstmt.setString(4, endTime);
                pstmt.executeUpdate();

            } else {
                throw new SQLException("no connection...");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());

        }
        System.out.println(startTime);
        System.out.println("system down");

    }
}
