package com.nhnacademy.aiot.gateway;

import com.nhnacademy.aiot.setting.BeforeSetting;

public class SimpleNodeRed {

    public static void main(String[] args) {
        BeforeSetting beforeSetting = new BeforeSetting();
        beforeSetting.checkCommandLine(args);
        beforeSetting.makeFlow();
        beforeSetting.connectWire();
        beforeSetting.nodeStart();
    }
}
