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

                    String type = (String) jsonObject.get("sensor");
                    String deviceEui = (String) jsonObject.get("deviceEui");

                    JSONObject obj = jsonArray.stream()
                            .filter(x -> x.getString("type").equals(type)
                                    && x.getString("deviceeui").equals(deviceEui))
                            .map(JSONObject.class::cast).findFirst().orElse(null);
                    int id = Integer.parseInt(obj.getString("id"));
                    redis.hsetPut("sensorInfo", String.valueOf(id), String
                            .valueOf(((JSONObject) (jsonObject.get("payload"))).get("value")));
                    System.out.println(id + ">>>" + jsonObject.get("place") + " > " + type + " : "
                            + ((JSONObject) (jsonObject.get("payload"))).get("value"));
                    output(0, new JsonMessage(new JSONObject(jsonObject.toString())));
                    int address = obj.getInt("address");
                    float value = jsonObject.getJSONObject("payload").getFloat("value");

                    byte[] request = SimpleMB.addMBAP(0, 1,
                            SimpleMB.makeWriteHoldingRegistersRequest(address, (int) value * 100));
                    output(1, new ByteMessage(request));
                } else {
                    byte[] byteObject = ((ByteMessage) message).getPayload();

                }
            }
        }
    }

}
