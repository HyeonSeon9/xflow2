package com.nhnacademy.aiot.modbus.server;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SimpleMB {

    public static byte[] intToByte(int number) {
        byte b1 = (byte) ((number >> 8) & 0x00FF);
        byte b2 = (byte) (number & 0x00FF);

        return new byte[] {b1, b2};
    }

    // 3 Request
    public static byte[] makeReadHoldingRegistersRequest(int address, int quantity) {
        byte[] frame = new byte[5];

        // PDU function code
        frame[0] = 0x03;

        // PDU Data Address
        byte[] addressByte = intToByte(address);
        frame[1] = addressByte[0];
        frame[2] = addressByte[1];

        // PDU Data Quantity
        byte[] quantityByte = intToByte(quantity);
        frame[3] = quantityByte[0];
        frame[4] = quantityByte[1];

        return frame;
    }

    // 3 Response
    public static byte[] makeReadHoldingRegistersResponse(int[] registers) {
        byte[] frame = new byte[1 + 1 + registers.length * 2];

        // PDU Function Code
        frame[0] = 0x03;

        // PDU Byte
        frame[1] = (byte) (registers.length * 2);

        for (int i = 0; i < registers.length; i++) {
            byte[] inputByte = intToByte(registers[i]);
            frame[2 + i * 2] = inputByte[0];
            frame[2 + i * 2 + 1] = inputByte[1];
        }
        return frame;
    }

    // 4 Request
    public static byte[] makeReadInputRegistersRequest(int address, int quantity) {
        byte[] frame = new byte[5];

        // PDU function code
        frame[0] = 0x04;

        // PDU Data Address
        byte[] addressByte = intToByte(address);
        frame[1] = addressByte[0];
        frame[2] = addressByte[1];

        // PDU Data Quantity
        byte[] quantityByte = intToByte(quantity);
        frame[3] = quantityByte[0];
        frame[4] = quantityByte[1];

        return frame;
    }

    // 4 Response
    public static byte[] makeReadInputRegisterResponse(int address, int quantity, int[] inputRegisters) {

        byte[] frame = new byte[1 + 1 + (quantity*2)];

        // PDU Function Code
        frame[0] = 0x04;

        // byte count
        frame[1] = (byte) (quantity * 2);

        // 하위 비트 16비트를 8비트 두 개로 나눠서 저장
        for (int i = 0; i < quantity; i++) {
            frame[2 + (i * 2)] = (byte) ((inputRegisters[address] >> 8) & 0xFF);
            frame[3 + (i * 2)] = (byte) ((inputRegisters[address++]) & 0xFF);
        }

        return frame;
    }

    // 6 Request = 6 Response
    public static byte[] makeWriteHoldingRegistersRequest(int address, int quantity) {
        byte[] frame = new byte[5];

        // PDU Function Code
        frame[0] = 0x06;
        
        // PDU Data Address
        byte[] addressByte = intToByte(address);
        frame[1] = addressByte[0];
        frame[2] = addressByte[1];

        // PDU Data Quantity
        byte[] quantityByte = intToByte(quantity);
        frame[3] = quantityByte[0];
        frame[4] = quantityByte[1];

        return frame;
    }

    // 16 Request
    public static byte[] makeWriteMultipleRegistersRequest(int address, int quantity, int byteCount, int... value) {
        byte[] frame = new byte[1 + 2 + 2 + 1 + byteCount];
        int[] intArray = value;

        // PDU Function Code
        frame[0] = 0x10;

        // PDU Data Address
        byte[] addressByte = intToByte(address);
        frame[1] = addressByte[0];
        frame[2] = addressByte[1];

        // PDU Data Quantity 1 ~ 123 (0000 0001 ~ 0111 1011)
        byte[] quantityByte = intToByte(quantity);
        frame[3] = quantityByte[0];
        frame[4] = quantityByte[1];

        // PDU Data byte count
        frame[5] = (byte) (intArray.length * 2);

        // PDU Data value
        for (int i = 0; i < intArray.length; i++) {
            byte[] valueByte = intToByte(intArray[i]);
            frame[6 + ( i * 2 )] = valueByte[0];
            frame[7 + ( i * 2 )] = valueByte[1];
        }

        return frame;
    }

    // 16 Response
    public static byte[] makeWriteMultipleRegistersResponse(int address, int quantity) {
        byte[] frame = new byte[1 + 2 + 2];

        ByteBuffer b = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);

        b.putInt(address);

        frame[0] = 0x10; // 16
        frame[1] = b.get(2);
        frame[2] = b.get(3);

        b.clear();

        b.putInt(quantity);

        frame[3] = b.get(2);
        frame[4] = b.get(3);

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
