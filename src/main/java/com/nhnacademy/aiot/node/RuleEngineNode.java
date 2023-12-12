package com.nhnacademy.aiot.node;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.nhnacademy.aiot.database.ReadPostgres;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.modbus.server.RedisServer;
import redis.clients.jedis.Jedis;

public class RuleEngineNode extends InputOutputNode {

    JSONArray jsonArray;
    Jedis jedis;

    public RuleEngineNode(String name, int count) {
        super(name, 1, count);
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    void preprocess() {
        RedisServer redisServer = new RedisServer();
        redisServer.connect();
        this.jedis = redisServer.getJedis();
        setJsonArray(ReadPostgres.getJsonArray());
    }


    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            while (getInputWire(i).hasMessage()) {
                Message message = getInputWire(0).get();
                org.json.JSONObject jsonObject =
                        new org.json.JSONObject(((JsonMessage) message).getPayload().toString());

                String type = (String) jsonObject.get("sensor");
                String deviceEui = (String) jsonObject.get("deviceEui");
                Object obj = jsonArray.stream()
                        .filter(x -> ((JSONObject) x).get("type").equals(type)
                                && ((JSONObject) x).get("deviceeui").equals(deviceEui))
                        .map(JSONObject.class::cast).findFirst().orElse(null);
                int id = Integer.parseInt((String) ((JSONObject) obj).get("id"));
                jedis.hset("sensorInfo", String.valueOf(id), String
                        .valueOf(((org.json.JSONObject) (jsonObject.get("payload"))).get("value")));
                System.out.println(id + ">>>" + jsonObject.get("place") + " > " + type + " : "
                        + ((org.json.JSONObject) (jsonObject.get("payload"))).get("value"));
                output(0, new JsonMessage(jsonObject));
            }
        }
    }

}
