package com.nhnacademy.aiot.gateway;

import com.nhnacademy.aiot.setting.BeforeSetting;
import com.nhnacademy.aiot.setting.Shutdown;

public class SimpleNodeRed {

    public static void main(String[] args) {
        Runtime r = Runtime.getRuntime();
        r.addShutdownHook(new Thread(new Shutdown()));
        BeforeSetting beforeSetting = new BeforeSetting();
        beforeSetting.checkCommandLine(args);
        beforeSetting.makeFlow();
        beforeSetting.connectWire();
        beforeSetting.nodeStart();
    }
}
