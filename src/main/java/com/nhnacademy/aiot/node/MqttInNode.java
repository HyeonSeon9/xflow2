package com.nhnacademy.aiot.node;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttInNode extends InputNode {
    private IMqttClient server = null;

    public MqttInNode(String name, int count) {
        super(name, count);
    }



    public void connectServer() {
        try {
            server = new MqttClient("tcp://ems.nhnacademy.com", super.getId().toString(), null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(1000);
            options.setWill("test/will", "Disconnected".getBytes(), 2, false);
            server.connect(options);
        } catch (MqttException e) {
            log.error("error-", e);
        }
    }

    public void serverSubscribe() {
        try {
            server.subscribe("#", (topic, msg) -> {
                JSONObject jsonObject = new JSONObject(msg);
                jsonObject.put("topic", topic);
                if (topic.contains("application")) {
                    JSONObject jsonPayLoad = new JSONObject(new String(msg.getPayload()));
                    jsonObject.put("payload", jsonPayLoad);
                    output(new JsonMessage(new JSONObject(jsonObject.toString())));
                } else {
                    output(new JsonMessage(new JSONObject(jsonObject.toString())));
                }
            });
        } catch (MqttException e) {
            log.error("error-", e);
        }
    }

    @Override
    void preprocess() {
        log.info("Node Start");
        connectServer();
        serverSubscribe();
    }

    @Override
    void process() {
        if (!server.isConnected()) {
            connectServer();
            serverSubscribe();
        }
    }

}
