package com.nhnacademy.aiot.gateway;

import com.nhnacademy.aiot.setting.BeforeSetting;
import com.nhnacademy.aiot.setting.Shutdown;

public class SimpleNodeRed {

    public static void main(String[] args) {
        BeforeSetting settingNode = new BeforeSetting();

        Runtime r = Runtime.getRuntime();
        r.addShutdownHook(new Thread(new Shutdown()));
        settingNode.checkCommandLine(args);
        settingNode.makeFlow();
        settingNode.connectWire();
        settingNode.nodeStart();
    }
}
