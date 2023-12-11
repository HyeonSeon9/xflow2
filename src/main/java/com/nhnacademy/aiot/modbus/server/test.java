package com.nhnacademy.aiot.modbus.server;

import java.io.IOException;
import com.nhnacademy.aiot.gateway.SettingNode;

public class test {
    public static void main(String[] args) throws IOException {

        SettingNode settingNode = new SettingNode();
        settingNode.makeFlow();
    }
}
