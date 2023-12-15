package com.nhnacademy.aiot.node;

import java.util.Arrays;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.modbus.client.Client;
import com.nhnacademy.aiot.modbus.server.SimpleMB;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusReadNode extends InputNode {
    private String dataType;
    private int quantity;
    private String serverName;
    private int unitid;
    private Client server;
    private int address;
    private static byte count = 0;

    public String getServerName() {
        return serverName;
    }

    public ModbusReadNode(String name, int port) {
        super(name, port);
        setInterval(6000);
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServer(Client server) {
        this.server = server;
    }

    public void setUnitId(int unitId) {
        this.unitid = unitId;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    void preprocess() {

    }

    @Override
    void process() {
        byte[] pdu = SimpleMB.makeReadInputRegistersRequest(address, quantity);
        byte[] request = SimpleMB.addMBAP(count++, server.getUnitId(), pdu);
        byte[] response = server.sendAndReceive(request);

        byte[] newResponse = new byte[response.length + 2];

        byte[] addressByte = SimpleMB.intToByte(address);

        newResponse[0] = addressByte[0];
        newResponse[1] = addressByte[1];

        System.arraycopy(response, 0, newResponse, 2, response.length);

        log.info("---------------------------{}", Arrays.toString(newResponse));
        log.info("---------------------------{}", SimpleMB.readTwoByte(response[9], response[10]));
        output(0, new ByteMessage(newResponse));

    }

}
