package com.nhnacademy.aiot.node;

import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;

public class RuleEngineNode extends InputOutputNode {

    public RuleEngineNode(String name, int count) {
        super(name, 1, count);
    }


    @Override
    void process() {
        if (((getInputWire(0) != null) && (getInputWire(0).hasMessage()))) {
            Message message = getInputWire(0).get();
            JSONObject jsonObject = new JSONObject(((JsonMessage) message).getPayload().toString());
        }
    }

}
