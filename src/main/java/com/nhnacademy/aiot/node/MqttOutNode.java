package com.nhnacademy.aiot.node;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttOutNode extends OutputNode {

    private IMqttClient local = null;

    public MqttOutNode(String name, int count) {
        super(name, count);
    }

    public void connectLocalHost() {
        try {
            local = new MqttClient("tcp://localhost", super.getId().toString(), null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(1000);
            options.setWill("test/will", "Disconnected".getBytes(), 2, false);
            local.connect(options);
        } catch (MqttException e) {
            log.error("error-", e);
        }
    }

    @Override
    void preprocess() {
        log.info("Node Start");
        connectLocalHost();
    }

    @Override
    void process() {

        if (((getInputWire(0) != null) && (getInputWire(0).hasMessage()))) {

            try {
                Message message = getInputWire(0).get();
                JSONObject jsonObject = ((JsonMessage) message).getPayload();

                String topic = jsonObject.getString("topic");
                JSONObject payload = jsonObject.getJSONObject("payload");

                MqttMessage mqttMessage = new MqttMessage();
                mqttMessage.setPayload(payload.toString().getBytes());

                // System.out.println(topic);
                // System.out.println(mqttMessage);
                local.publish(topic, mqttMessage);
            } catch (MqttException e) {
                log.error("error-", e);
            }
        }
    }

}
