package com.nhnacademy.aiot.node;

import java.util.List;
import org.json.JSONObject;
import com.nhnacademy.aiot.database.ReadPostgres;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.modbus.client.Redis;
import com.nhnacademy.aiot.modbus.server.SimpleMB;

public class RuleEngineNode extends InputOutputNode {

    List<JSONObject> jsonArray;
    Redis redis;
    String redisName;

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

    public JSONObject MqttToModbus(JSONObject jsonObject, int address) {
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
        System.out.println("insert into id" + id);
        redis.hsetPut("sensorInfo", String.valueOf(id), String.valueOf(value));
    }

    @Override
    void preprocess() {
        redis.connect();
        setJsonArray(ReadPostgres.getJsonArray());

    }



    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            while (getInputWire(i).hasMessage()) {
                Message message = getInputWire(i).get();
                if (message instanceof JsonMessage) {
                    JSONObject jsonObject =
                            new JSONObject(((JsonMessage) message).getPayload().toString());
                    JSONObject insertElement = findByTypeAndDeviceEui(jsonObject);
                    float value = jsonObject.getJSONObject("payload").getFloat("value");
                    redisInsert(insertElement, value);
                    output(0, new JsonMessage(new JSONObject(jsonObject.toString())));
                    int address = insertElement.getInt("address");
                    JSONObject modbusRequest = MqttToModbus(jsonObject, address);
                    byte[] request = SimpleMB.addMBAP(0, 1,
                            SimpleMB.makeWriteHoldingRegistersRequest(address, (int) value * 100));
                    // output(1, new ByteMessage(request));
                    output(1, new JsonMessage(new JSONObject(modbusRequest.toString())));
                } else {
                    byte[] byteObject = ((ByteMessage) message).getPayload();

                }
            }
        }
    }

}
