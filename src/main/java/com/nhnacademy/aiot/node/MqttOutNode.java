package com.nhnacademy.aiot.node;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.modbus.client.Broker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MqttOutNode extends OutputNode {

    private Broker broker;
    private String topicSplit = "#";
    private String brokerName;

    public MqttOutNode(String name, int count) {
        super(name, count);
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

    @Override
    void preprocess() {
        log.info("Node Start");
    }

    @Override
    void process() {

        if (!broker.getBroker().isConnected()) {
            connectServer();
        }

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
                broker.getBroker().publish(topic, mqttMessage);
            } catch (MqttException e) {
                log.error("error-", e);
            }
        }
    }

}
