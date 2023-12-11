package com.nhnacademy.aiot.node;

import java.util.Map;
import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlaceTranslatorNode extends InputOutputNode {
    Map<String, String> placeInfo;

    public PlaceTranslatorNode(String name, int count) {
        super(name, 1, count);
    }

    public void setPlaceInfo(Map<String, String> placeInfo) {
        this.placeInfo = placeInfo;
    }

    @Override
    void preprocess() {
        log.info("Node Start");
    }

    @Override
    void process() {
        if (((getInputWire(0) != null) && (getInputWire(0).hasMessage()))) {
            Message message = getInputWire(0).get();
            JSONObject jsonObject = new JSONObject(((JsonMessage) message).getPayload().toString());
            jsonObject.put("place", placeInfo.get(jsonObject.get("place")));
            jsonObject.put("prev", "Place");
            log.info("패킷전송");
            output(new JsonMessage(new JSONObject(jsonObject.toString())));
        }
    }
}
