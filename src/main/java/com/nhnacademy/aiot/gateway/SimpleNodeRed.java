package com.nhnacademy.aiot.gateway;

public class SimpleNodeRed {

    public static void main(String[] args) {
        BeforeSetting beforeSetting = new BeforeSetting();
        beforeSetting.checkCommandLine(args);
        beforeSetting.makeFlow();
        beforeSetting.connectWire();
        beforeSetting.nodeStart();
    }
}
