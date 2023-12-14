package com.nhnacademy.aiot.node;

import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.modbus.client.Client;
import com.nhnacademy.aiot.modbus.server.SimpleMB;

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
        setInterval(10000000);
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
        byte[] pdu = SimpleMB.makeReadHoldingRegistersRequest(address, quantity);
        byte[] request = SimpleMB.addMBAP(count++, server.getUnitId(), pdu);
        byte[] response = server.sendAndReceive(request);

        output(0, new ByteMessage(response));

    }

}
