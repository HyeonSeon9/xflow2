package com.nhnacademy.aiot.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.modbus.server.ModbusServer;
import com.nhnacademy.aiot.modbus.server.SimpleMB;

public class ModbusSlaveServerNode extends OutputNode {

    int inputWireCount;
    int[] holdingRegisters;
    int[] inputRegisters;
    int serverPort;
    private ServerSocket serverSocket;

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

    public void launchServer() {
        try {
            this.serverSocket = new ServerSocket(serverPort);
            ModbusServer modbusServer =
                    new ModbusServer(serverSocket, holdingRegisters, inputRegisters);
            System.out.println("ServerSocket 11111111111");
            modbusServer.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    void preprocess() {
        inputWireCount = getInputWireCount();
    }

    @Override
    void process() {
        for (int i = 0; i < inputWireCount; i++) {
            if ((getInputWire(i) != null) && (getInputWire(i).hasMessage())) {
                Message message = getInputWire(i).get();
                if (message instanceof JsonMessage) {

                } else {
                    ByteMessage byteMessage = (ByteMessage) message;
                    byte[] msgArray = byteMessage.getPayload();

                    int address = SimpleMB.readTwoByte(msgArray[2], msgArray[3]);
                    int value = SimpleMB.readTwoByte(msgArray[4], msgArray[5]); // value는 100이 곱해져
                                                                                // 있음

                    if (msgArray[6] == (byte) 1) {
                        inputRegisters[address] = value;
                        holdingRegisters[201] = 9999;
                        System.out.println("inputRegister[address] 값 : " + inputRegisters[address]);
                    } else {
                        holdingRegisters[address] = value;
                    }
                }
            }
        }
    }

}
