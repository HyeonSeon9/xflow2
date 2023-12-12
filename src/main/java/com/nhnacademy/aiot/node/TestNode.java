package com.nhnacademy.aiot.node;

import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;

public class TestNode extends InputNode {

    public TestNode(String name, int count) {
        super(name, count);
        setInterval(3000);
    }

    @Override
    void process() {
        output(0, new JsonMessage(new JSONObject().put("test", "hi " + getName())));
        output(1, new JsonMessage(new JSONObject().put("hello", "world " + this.getName())));
    }

}
