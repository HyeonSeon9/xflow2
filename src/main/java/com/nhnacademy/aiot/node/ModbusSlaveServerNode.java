package com.nhnacademy.aiot.node;

import java.util.Arrays;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.Message;

public class ModbusSlaveServerNode extends OutputNode {
    
    int inputWireCount;
    int[] holdingRegisters;
    int[] inputRegisters;
    int serverPort;

    public ModbusSlaveServerNode(String name, int count) {
        super(name, count);
    }

    public void setHoldingRegisters(int holdingBufferSize) {
        this.holdingRegisters = new int[holdingBufferSize];
    }

    public void setInputRegisters(int inputBufferSize) {
        this.inputRegisters = new int[inputBufferSize];
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    void preprocess() {
        inputWireCount = getInputWireCount();
    }

    @Override
    void process() {
        for (int i = 0; i < inputWireCount; i++) {
            if ((getInputWire(i) != null) && (getInputWire(i).hasMessage())) {
                ByteMessage message = (ByteMessage)getInputWire(i).get();
                byte[] msgArray = message.getPayload();

                System.out.println("--------------"+Arrays.toString(msgArray));
            }
        }
    }

}
