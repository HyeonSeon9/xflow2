package com.nhnacademy.aiot.message;

import org.json.JSONObject;

public class JsonMessage extends Message {
    JSONObject payload;

    public JsonMessage(JSONObject payload) {
        this.payload = payload;
    }

    public JSONObject getPayload() {
        return this.payload;
    }

}
