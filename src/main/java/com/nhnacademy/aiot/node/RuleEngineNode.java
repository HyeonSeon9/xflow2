package com.nhnacademy.aiot.node;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.json.JSONObject;
import com.nhnacademy.aiot.database.ReadPostgres;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.modbus.server.RedisServer;
import redis.clients.jedis.Jedis;

public class RuleEngineNode extends InputOutputNode {

    List<JSONObject> jsonArray;
    Jedis jedis;
    Stream<JSONObject> stream;

    public RuleEngineNode(String name, int count) {
        super(name, 1, count);
    }

    public void setJsonArray(List<JSONObject> jsonArray) {
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
                if(message instanceof JsonMessage){
                    
                }
                JSONObject jsonObject =
                        new JSONObject(((JsonMessage) message).getPayload().toString());

                String type = (String) jsonObject.get("sensor");
                String deviceEui = (String) jsonObject.get("deviceEui");

                JSONObject obj = jsonArray.stream()
                        .filter(x -> x.getString("type").equals(type)
                                && x.getString("deviceeui").equals(deviceEui))
                        .map(JSONObject.class::cast).findFirst().orElse(null);
                int id = Integer.parseInt(obj.getString("id"));
                jedis.hset("sensorInfo", String.valueOf(id),
                        String.valueOf(((JSONObject) (jsonObject.get("payload"))).get("value")));
                System.out.println(id + ">>>" + jsonObject.get("place") + " > " + type + " : "
                        + ((JSONObject) (jsonObject.get("payload"))).get("value"));
                output(0, new JsonMessage(jsonObject));

                JSONObject modObject = new JSONObject();
                int unitId = obj.getInt("unitid");
                int address = obj.getInt("address");
                int value = 0;
                
                // float형태로 옴
                Object valueObject = jsonObject.getJSONObject("payload").get("value");
                
                if (valueObject instanceof BigDecimal) {
                    BigDecimal valueDecimal = (BigDecimal) valueObject;
                    value = valueDecimal.multiply(new BigDecimal(100)).intValue();
                    System.out.println("--------------" + value);
                } else if (valueObject instanceof Integer) {
                    value = (Integer) valueObject * 100;
                }

                modObject.put("unitid", unitId);
                modObject.put("address", address);
                modObject.put("value", value);  // 100이 곱해져 있는 상태
                modObject.put("register", "1"); // input일 경우 input, 0일 경우 holding

                output(1, new JsonMessage(modObject));
            }
        }
    }

}
