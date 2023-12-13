package com.nhnacademy.aiot.node;

import org.json.JSONObject;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.modbus.server.SimpleMB;

public class ModbusMapperNode extends InputOutputNode {
    
    int inputWireCount;
    byte[] byteArray;

    public ModbusMapperNode(String name, int count) {
        super(name, 1, count);
    }

    public void changeToByte(int unitId, int address, int value) {
        byte[] unitIdByte = SimpleMB.intToByte(unitId);

        byteArray[0] = unitIdByte[0];
        byteArray[1] = unitIdByte[1];

        byte[] addressByte = SimpleMB.intToByte(address);

        byteArray[2] = addressByte[0];
        byteArray[3] = addressByte[1];
                
        byte[] valueArray = SimpleMB.intToByte(value);

        byteArray[4] = valueArray[0];
        byteArray[5] = valueArray[1];

        sendNext(byteArray);
    }

    public void sendNext(byte[] byteArray) {
        output(0, new ByteMessage(byteArray));
    }

    @Override
    void preprocess() {
        inputWireCount = getInputWireCount();
    }

    @Override
    void process() {
        for (int i = 0; i < inputWireCount; i++) {
            if (getInputWire(i) != null && getInputWire(i).hasMessage()) {
                Message message = getInputWire(i).get();
                JSONObject jsonObject = new JSONObject(((JsonMessage) message).getPayload().toString());

                byteArray = new byte[6];

                int unitId = jsonObject.getInt("unitid");
                int address = jsonObject.getInt("address");
                int value = jsonObject.getInt("value");
                
                changeToByte(unitId, address, value);
            }
        }
    }

}
