package com.nhnacademy.aiot.modbus.client;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Broker {
    private static final String TCP = "tcp://";

    IMqttClient broker;

    private String host;
    private String name;

    private boolean autoConnect;
    private boolean cleanSession;

    private int port;
    private int keepAlive;

    public Broker(String name, String host, int port, int keepAlive, boolean autoConnect,
            boolean cleanSession) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.keepAlive = keepAlive;
        this.autoConnect = autoConnect;
        this.cleanSession = cleanSession;
    }


    public void connect() {
        try {
            broker = new MqttClient(TCP + host, UuidCreator.getTimeBased().toString(), null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(autoConnect);
            options.setCleanSession(cleanSession);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(keepAlive);
            broker.connect(options);
        } catch (MqttException e) {
            log.error("error-", e);
        }
    }

    public IMqttClient getBroker() {
        return broker;
    }
}
