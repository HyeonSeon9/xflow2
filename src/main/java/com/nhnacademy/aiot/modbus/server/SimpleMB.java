package com.nhnacademy.aiot.modbus.server;

public class SimpleMB {

    public static byte[] intToByte(int number) {
        byte b1 = (byte) ((number >> 8) & 0x00FF);
        byte b2 = (byte) (number & 0x00FF);

        return new byte[] {b1, b2};
    }

    public static byte[] makeReadHoldingRegistersRequest(int address, int quantity) {
        byte[] frame = new byte[5];

        // PDU의 function code
        frame[0] = 0x03;

        // PDU의 data
        byte[] addressByte = intToByte(address);
        frame[1] = addressByte[0];
        frame[2] = addressByte[1];

        byte[] quantityByte = intToByte(quantity);
        frame[3] = quantityByte[0];
        frame[4] = quantityByte[1];

        return frame;
    }

    public static byte[] makeWriteHoldingRegistersRequest(int address, int value) {
        byte[] frame = new byte[5];

        // PDU의 function code
        frame[0] = 0x06;

        // PDU의 data
        byte[] addressByte = intToByte(address);
        frame[1] = addressByte[0];
        frame[2] = addressByte[1];

        byte[] quantityByte = intToByte(value);
        frame[3] = quantityByte[0];
        frame[4] = quantityByte[1];

        return frame;
    }

    public static byte[] makeReadHoldingRegistersResponse(int[] registers) {
        byte[] frame = new byte[1 + 1 + registers.length * 2];

        // PDU의 Function Code
        frame[0] = 0x03;

        // Length
        frame[1] = (byte) (registers.length * 2);

        for (int i = 0; i < registers.length; i++) {
            byte[] inputByte = intToByte(registers[i]);
            frame[2 + i * 2] = inputByte[0];
            frame[2 + i * 2 + 1] = inputByte[1];
        }
        return frame;
    }

    public static byte[] addMBAP(int transactionId, int unitId, byte[] pdu) {
        byte[] adu = new byte[7 + pdu.length];

        byte[] transactionIdByte = intToByte(transactionId);
        byte[] pduLength = intToByte(pdu.length + 1);

        adu[0] = transactionIdByte[0];
        adu[1] = transactionIdByte[1];
        adu[2] = 0;
        adu[3] = 0;
        adu[4] = pduLength[0];
        adu[5] = pduLength[1];

        adu[6] = (byte) unitId;

        System.arraycopy(pdu, 0, adu, 7, pdu.length);

        return adu;
    }

    public static int readTwoByte(byte first, byte second) {
        return ((first << 8) & 0xFF00 | second & 0x00FF);
    }

    public static int[] addByte(byte[] inputByte) {
        int byteCount = inputByte[8];
        int[] result = new int[byteCount / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = readTwoByte(inputByte[9 + i * 2], inputByte[9 + i * 2 + 1]);
        }
        return result;
    }
}
