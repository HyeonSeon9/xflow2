package com.nhnacademy.aiot.message;

public class ByteMessage extends Message {
    byte[] payload;

    public ByteMessage(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getPayload() {
        return this.payload;
    }

}
