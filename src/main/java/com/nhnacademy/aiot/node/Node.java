package com.nhnacademy.aiot.node;

import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import com.github.f4b6a3.uuid.UuidCreator;

public abstract class Node {
    private static int count;
    UUID id;
    String name;
    Logger log;

    Node() {
        this("Node" + count, UuidCreator.getTimeBased());
    }

    Node(JSONObject json) {
        if (json.has("id")) {
            id = UuidCreator.fromString((String) json.get("id"));
        } else {
            id = UuidCreator.getTimeBased();
        }
    }

    Node(UUID id) {
        this(id.toString(), id);
    }

    Node(String name) {
        this(name, UuidCreator.getTimeBased());
    }

    Node(String name, UUID id) {
        count++;
        this.id = id;
        this.name = name;
        log = LogManager.getLogger(name);

        log.trace("create node : {}", id);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        log = LogManager.getLogger(name);
    }

    public static int getCount() {
        return count;
    }

    public JSONObject getJson() {
        JSONObject object = new JSONObject();

        object.put("id", id);
        object.put("name", name);

        return object;
    }
}
