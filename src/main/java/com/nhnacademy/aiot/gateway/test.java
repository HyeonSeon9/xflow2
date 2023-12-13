package com.nhnacademy.aiot.gateway;

import com.nhnacademy.aiot.node.MqttInNode;
import com.nhnacademy.aiot.node.MqttOutNode;
import com.nhnacademy.aiot.node.PostgresOutNode;
import com.nhnacademy.aiot.node.ReduceTopicNode;
import com.nhnacademy.aiot.node.SplitNode;
import com.nhnacademy.aiot.wire.BufferedWire;

public class test {

    public static void main(String[] args) {
        MqttInNode mqttInNode = new MqttInNode("mqttIn", 1);
        SplitNode splitNode = new SplitNode("split", 1);
        ReduceTopicNode reduceTopicNode = new ReduceTopicNode("reduce", 1);
        MqttOutNode mqttOutNode = new MqttOutNode("mqttOut", 1);
        PostgresOutNode postgresOutNode = new PostgresOutNode("postOut", 1);
        BufferedWire wire1 = new BufferedWire();
        BufferedWire wire2 = new BufferedWire();

        mqttInNode.connectOutputWire(0, wire1);
        splitNode.connectInputWire(0, wire1);
        splitNode.connectOutputWire(0, wire1);

        reduceTopicNode.connectInputWire(0, wire1);
        reduceTopicNode.connectOutputWire(0, wire2);
        postgresOutNode.connectInputWire(0, wire2);

        mqttInNode.start();
        splitNode.start();
        reduceTopicNode.start();
        postgresOutNode.start();
    }
}
