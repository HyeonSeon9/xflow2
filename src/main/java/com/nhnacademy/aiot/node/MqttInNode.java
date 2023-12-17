package com.nhnacademy.aiot.node;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.modbus.client.Broker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttInNode extends InputNode {
    private Broker broker;
    private String topicSplit = "#";
    private String brokerName;

    public MqttInNode(String name, int count) {
        super(name, count);
        setInterval(10000000);
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public void setTopic(String topicSplit) {
        this.topicSplit = topicSplit;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public void connectServer() {
        broker.connect();
    }

    public void serverSubscribe() {
        try {
            broker.getBroker().subscribe("#", (topic, msg) -> {
                JSONObject jsonObject = new JSONObject(msg);
                jsonObject.put("topic", topic);
                if (topic.contains("application")) {
                    JSONObject jsonPayLoad = new JSONObject(new String(msg.getPayload()));
                    jsonObject.put("payload", jsonPayLoad);
                    output(0, new JsonMessage(new JSONObject(jsonObject.toString())));
                } else {
                    output(0, new JsonMessage(new JSONObject(jsonObject.toString())));
                }
            });
        } catch (MqttException e) {
            log.error("error-", e);
        }
    }

    @Override
    void preprocess() {
        log.info("Node Start");
        serverSubscribe();
    }

    @Override
    void process() {
        if (!broker.getBroker().isConnected()) {
            connectServer();
            serverSubscribe();
        }
    }

}
