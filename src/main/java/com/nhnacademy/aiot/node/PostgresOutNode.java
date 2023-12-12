package com.nhnacademy.aiot.node;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;

public class PostgresOutNode extends OutputNode {
    private Connection connect;
    private Statement stmt;
    private ResultSet rs;
    String url = "jdbc:postgresql://localhost:5432/xflow";
    String user = "postgres";
    String password = "root";
    static int temp = 100;
    static int humi = 200;

    public PostgresOutNode(String name, int count) {
        super(name, count);
        try {
            setup();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setup() throws SQLException {
        this.connect = DriverManager.getConnection(url, user, password);
        this.stmt = connect.createStatement();
    }

    @Override
    void process() {
        if (((getInputWire(0) != null) && (getInputWire(0).hasMessage()))) {
            Message message = getInputWire(0).get();
            JSONObject jsonObject = ((JsonMessage) message).getPayload();
            String type = jsonObject.getString("sensor");
            String deviceEui = jsonObject.getString("deviceEui");
            String site = jsonObject.getString("site");
            String branch = jsonObject.getString("branch");
            String place = jsonObject.getString("place");
            String sql =
                    "INSERT INTO sensorinfo (id, type, deviceeui, site, branch, place, unitid, address) VALUES (default, ?, ?, ?, ?, ?, 0, ?)";
            String search = String.format(
                    "SELECT * FROM sensorinfo WHERE type = '%s' AND deviceeui = '%s';", type,
                    deviceEui);

            System.out.println("here");
            try {
                this.rs = stmt.executeQuery(search);
                if (!rs.next()) {
                    try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
                        int current = 0;
                        if (type.equals("temperature")) {
                            current = temp++;
                        } else {
                            current = humi++;
                        }
                        pstmt.setString(1, type);
                        pstmt.setString(2, deviceEui);
                        pstmt.setString(3, site);
                        pstmt.setString(4, branch);
                        pstmt.setString(5, place);
                        pstmt.setInt(6, current);
                        String generatedSql = pstmt.toString();
                        System.out.println("Generated SQL: " + generatedSql);
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace(); // 또는 원하는 방식으로 예외 처리
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
