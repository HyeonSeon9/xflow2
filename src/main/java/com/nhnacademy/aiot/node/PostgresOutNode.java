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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgresOutNode extends OutputNode {
    private Connection connect;
    private Statement stmt;
    private ResultSet rs;
    String url = "jdbc:postgresql://localhost:5432/xflow";
    String user = "postgres";
    String password = "root";
    static int temp = 100;
    static int humi = 200;
    static int unit = 100;
    static int unit2 = 100;

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
        for (int i = 0; i < getInputWireCount(); i++) {
            while (getInputWire(i).hasMessage()) {
                Message message = getInputWire(i).get();
                JSONObject jsonObject = ((JsonMessage) message).getPayload();
                String type = jsonObject.getString("sensor");
                String deviceEui = jsonObject.getString("deviceEui");
                String site = jsonObject.getString("site");
                String branch = jsonObject.getString("branch");
                String place = jsonObject.getString("place");
                String sql =
                        "INSERT INTO sensorinfo (id, type, deviceeui, site, branch, place, unitid, address,virtualId,virtualAddress) VALUES (default, ?, ?, ?, ?, ?, 1, ?,?,?)";
                String search = String.format(
                        "SELECT * FROM sensorinfo WHERE type = '%s' AND deviceeui = '%s';", type,
                        deviceEui);

                System.out.println("here");
                try {
                    this.rs = stmt.executeQuery(search);
                    if (!rs.next()) {
                        try (PreparedStatement pstmt = connect.prepareStatement(sql)) {
                            int current = 0;
                            int currentUnit = 0;
                            int virtualid = 0;
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
                            if (place.equals("강의실 A") || place.equals("강의실 B")) {
                                virtualid = 1;
                                currentUnit = unit++;
                            } else {
                                virtualid = 2;
                                currentUnit = unit2++;
                            }
                            pstmt.setInt(7, virtualid);
                            pstmt.setInt(8, currentUnit);
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
}
