package com.nhnacademy.aiot.setting;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
        String url = "jdbc:postgressql://localhost:5432/xflow";
        String user = "postgres";
        String password = "root";
        String sql =
                "INSERT INTO packetinfo (id, send, receive, starttime, endtime) VALUES (default, ?, ?, ?, ?)";

        LocalDateTime end = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String endTime = end.format(formatter);
        try (Connection connect = DriverManager.getConnection(url, user, password);
                PreparedStatement pstmt = connect.prepareStatement(sql);) {
            pstmt.setInt(1, RuleEngineNode.messageCount);
            pstmt.setInt(2, 0);
            pstmt.setString(3, startTime);
            pstmt.setString(4, endTime);
            pstmt.executeUpdate();
            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
