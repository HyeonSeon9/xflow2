package com.nhnacademy.aiot.node;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;
import com.nhnacademy.aiot.database.ReadPostgres;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.modbus.client.Redis;
import com.nhnacademy.aiot.modbus.server.SimpleMB;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class RuleEngineNode extends InputOutputNode {

    List<JSONObject> jsonArray;
    Redis redis;
    String redisName;
    static int messageCount = 0;

    public RuleEngineNode(String name, int count) {
        super(name, 1, count);
    }

    public void setJsonArray(List<JSONObject> jsonArray) {
        this.jsonArray = jsonArray;
    }

    public void setRedisName(String redisName) {
        this.redisName = redisName;
    }

    public String getRedisName() {
        return redisName;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }

    public static int getMessageCount() {
        return messageCount;
    }

    public JSONObject makeModbusInsert(JSONObject jsonObject, int address) {
        JSONObject modbusRequest = new JSONObject();
        float value = jsonObject.getJSONObject("payload").getFloat("value");

        modbusRequest.put("value", value);
        modbusRequest.put("address", address);
        modbusRequest.put("register", "input");
        return modbusRequest;
    }


    public JSONObject findByTypeAndDeviceEui(JSONObject jsonObject) {
        String type = (String) jsonObject.get("sensor");
        String deviceEui = (String) jsonObject.get("deviceEui");

        JSONObject obj = jsonArray.stream()
                .filter(x -> x.getString("type").equals(type)
                        && x.getString("deviceeui").equals(deviceEui))
                .map(JSONObject.class::cast).findFirst().orElse(null);
        return obj;
    }



    public void redisInsert(JSONObject jsonObject, float value) {
        int id = Integer.parseInt(jsonObject.getString("id"));
        redis.hsetPut("sensorInfo", String.valueOf(id), String.valueOf(value));
    }

    @Override
    void preprocess() {
        redis.connect();
        setJsonArray(ReadPostgres.getJsonArray());

    }

    public void mqttToMqttAndModbus(JSONObject jsonObject) {
        JSONObject insertElement = findByTypeAndDeviceEui(jsonObject);
        float value = jsonObject.getJSONObject("payload").getFloat("value");
        redisInsert(insertElement, value);
        output(0, new JsonMessage(new JSONObject(jsonObject.toString())));
        int address = insertElement.getInt("address");
        JSONObject modbusRequest = makeModbusInsert(jsonObject, address);
        output(1, new JsonMessage(new JSONObject(modbusRequest.toString())));
    }

    public void modBusToModbusAndMqtt(byte[] byteObject) {
        int virtualId = byteObject[8];
        int virtualAddress = SimpleMB.readTwoByte(byteObject[0], byteObject[1]);
        float value = (float) ((SimpleMB.readTwoByte(byteObject[byteObject.length - 2],
                byteObject[byteObject.length - 1])) * 0.01);

        JSONObject jsonObject = jsonArray.stream()
                .filter(x -> x.getInt("virtualaddress") == (virtualAddress)
                        && x.getInt("virtualid") == virtualId)
                .map(JSONObject.class::cast).findFirst().orElse(null);
        redisInsert(jsonObject, value);
        int address = jsonObject.getInt("address");

        JSONObject modbusRequest = new JSONObject();
        modbusRequest.put("value", value);
        modbusRequest.put("address", address);
        modbusRequest.put("register", "input");
        System.out.println(modbusRequest);
        output(1, new JsonMessage(new JSONObject(modbusRequest.toString())));

        JSONObject mqttRequest = new JSONObject();
        mqttRequest.put("site", jsonObject.getString("site"));
        mqttRequest.put("deviceEui", jsonObject.getString("deviceeui"));
        mqttRequest.put("branch", jsonObject.getString("branch"));
        mqttRequest.put("place", jsonObject.getString("place"));
        mqttRequest.put("sensor", jsonObject.getString("type"));
        JSONObject payload = new JSONObject();
        payload.put("time", new Date().getTime());
        payload.put("value", value);
        mqttRequest.put("payload", payload);
        output(0, new JsonMessage(new JSONObject(mqttRequest.toString())));
    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            while (getInputWire(i).hasMessage()) {
                Message message = getInputWire(i).get();
                messageCount++;
                if (message instanceof JsonMessage) {
                    JSONObject jsonObject =
                            new JSONObject(((JsonMessage) message).getPayload().toString());
                    mqttToMqttAndModbus(jsonObject);
                } else {
                    // ((ByteMessage) message).getPayload();
                    byte[] byteObject = Arrays.copyOf(((ByteMessage) message).getPayload(),
                            ((ByteMessage) message).getPayload().length);
                    modBusToModbusAndMqtt(byteObject);
                }
            }
        }
    }

}
