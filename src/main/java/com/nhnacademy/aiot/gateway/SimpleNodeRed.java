package com.nhnacademy.aiot.gateway;

public class SimpleNodeRed {

    public static void main(String[] args) {
        SettingNode settingNode = new SettingNode();
        settingNode.checkCommandLine(args);
        settingNode.makeFlow();
        settingNode.connectWire();
        settingNode.nodeStart();
    }
}
