package com.nhnacademy.aiot.wire;

import com.nhnacademy.aiot.message.Message;

public interface Wire {

    public void put(Message message);

    public boolean hasMessage();

    public Message get();
}
