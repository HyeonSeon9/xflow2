package com.nhnacademy.aiot.node;

import java.util.Arrays;
import org.json.JSONObject;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.modbus.server.SimpleMB;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DebugNode extends OutputNode {

    public DebugNode(String name, int count) {
        super(name, count);
    }

    @Override
    void preprocess() {
        log.info("Node Start");
    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            while (getInputWire(i).hasMessage()) {
                Message message = getInputWire(i).get();
                if (message instanceof JsonMessage) {
                    JSONObject jsonObject = ((JsonMessage) message).getPayload();
                    log.info("{}", jsonObject);
                } else {
                    byte[] byteObject = ((ByteMessage) message).getPayload();
                    if (byteObject[7] == 3) {
                        log.info("{}", Arrays.toString(SimpleMB.addByte(byteObject)));
                    } else {
                        log.info("{}", Arrays.toString(byteObject));
                    }
                }
            }
        }

    }
}
