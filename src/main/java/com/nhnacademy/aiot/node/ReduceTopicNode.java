package com.nhnacademy.aiot.node;

import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReduceTopicNode extends InputOutputNode {

    public ReduceTopicNode(String name, int count) {
        super(name, 1, count);
    }

    public String makeTopic(JSONObject jsonObject) {
        String site = jsonObject.getString("site");
        String branch = jsonObject.getString("branch");
        String devEui = jsonObject.getString("deviceEui");
        String place = jsonObject.getString("place");
        String sensor = jsonObject.getString("sensor");

        return String.format("data/s/%s/b/%s/d/%s/p/%s/e/%s", site, branch, devEui, place, sensor);
    }
    // data/s/nhnacademy/b/gyeongnam/p/class_b/e/spl
    // data/d/24e124136d 151547/p/창고/e/temperature:msg.payload:

    @Override
    void preprocess() {
        log.info("Node Start");
    }

    @Override
    void process() {

        if (((getInputWire(0) != null) && (getInputWire(0).hasMessage()))) {
            Message message = getInputWire(0).get();
            JSONObject jsonObject = new JSONObject(((JsonMessage) message).getPayload().toString());

            String topic = makeTopic(jsonObject);

            jsonObject.put("topic", topic);

            jsonObject.put("prev", "Reduce");
            log.info("패킷전송");
            output(new JsonMessage(new JSONObject(jsonObject.toString())));
        }
    }

}
