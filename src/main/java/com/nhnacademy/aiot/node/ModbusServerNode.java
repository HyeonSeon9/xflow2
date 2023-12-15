package com.nhnacademy.aiot.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import org.json.JSONObject;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.modbus.server.ModbusServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusServerNode extends InputOutputNode {


    private int[] holdingRegisters;
    private int[] inputRegisters;
    private int serverPort;
    private ServerSocket serverSocket;
    private Socket socket;



    public ModbusServerNode(String name, int count) {
        super(name, 1, count);
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
            modbusServer.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    void process() {
        for (int i = 0; i < getInputWireCount(); i++) {
            while (getInputWire(i).hasMessage()) {
                Message message = getInputWire(i).get();
                if (message instanceof JsonMessage) {
                    JSONObject jsonObject =
                            new JSONObject(((JsonMessage) message).getPayload().toString());
                    float value = jsonObject.getFloat("value");
                    int address = jsonObject.getInt("address");
                    if (jsonObject.getString("register").equals("input")) {
                        inputRegisters[address] = (int) (value * 100);
                    }
                } else {
                    byte[] data = ((ByteMessage) message).getPayload();
                    log.info("{}", Arrays.toString(data));

                }
            }
        }
    }
}
