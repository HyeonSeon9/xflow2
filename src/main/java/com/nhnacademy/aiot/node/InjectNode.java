package com.nhnacademy.aiot.node;

import com.nhnacademy.aiot.message.ByteMessage;

public class InjectNode extends InputNode {
    public InjectNode(String name, int count) {
        super(name, count);
        setInterval(1000);
    }

    @Override
    void process() {
        byte[] bufferOut = new byte[] {0, 1, 0, 0, 0, 6, 1, 3, 0, 0, 0, 5};
        output(new ByteMessage(bufferOut));
    }
}
