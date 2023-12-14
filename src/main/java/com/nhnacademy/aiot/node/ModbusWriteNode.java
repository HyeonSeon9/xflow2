package com.nhnacademy.aiot.node;

import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.modbus.client.Client;
import com.nhnacademy.aiot.modbus.server.SimpleMB;

public class ModbusWriteNode extends InputOutputNode {
    private String dataType;
    private int quantity;
    private String serverName;
    private int unitid;
    private Client server;
    private int address;
    private static byte count = 0;

    public ModbusWriteNode(String name, int count) {
        super(name, 1, count);
        setInterval(1000000);
    }

    public String getServerName() {
        return serverName;
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
        byte[] pdu = SimpleMB.makeWriteHoldingRegistersRequest(count, 5);
        byte[] request = SimpleMB.addMBAP(count++, server.getUnitId(), pdu);
        byte[] receive = server.sendAndReceive(request);
        output(0, new ByteMessage(receive));
    }

}
