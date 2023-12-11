package com.nhnacademy.aiot.wire;

import java.util.LinkedList;
import java.util.Queue;
import com.nhnacademy.aiot.message.Message;

public class BufferedWire implements Wire {
    Queue<Message> messageQueue;

    public BufferedWire() {
        super();
        messageQueue = new LinkedList<>();
    }

    public void put(Message message) {
        messageQueue.add(message);
    }

    public boolean hasMessage() {
        return !messageQueue.isEmpty();
    }

    public Message get() {
        return messageQueue.poll();
    }
}
