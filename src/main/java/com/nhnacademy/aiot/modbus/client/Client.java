package com.nhnacademy.aiot.modbus.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    private String host;
    private String name;
    private int port;
    private int unitId;

    public Client(String name, String host, int port, int unitId) {
        this.host = host;
        this.name = name;
        this.port = port;
        this.unitId = unitId;
    }


    public byte[] sendAndReceive(byte[] request) {
        byte[] inputByte = null;
        try (Socket socket = new Socket(host, port);
                BufferedOutputStream outputStream =
                        new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream inputStream =
                        new BufferedInputStream(socket.getInputStream())) {
            outputStream.write(request);
            outputStream.flush();
            byte[] inputBuffer = new byte[1024];
            int receivedLength = inputStream.read(inputBuffer, 0, inputBuffer.length);
            inputByte = Arrays.copyOfRange(inputBuffer, 0, receivedLength);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return inputByte;
    }

    public int getUnitId() {
        return unitId;
    }



    // public static void main(String[] args) {
    // try (Socket socket = new Socket(host, port);
    // BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
    // BufferedOutputStream outputStream =
    // new BufferedOutputStream(socket.getOutputStream());) {

    // byte[] request = new byte[] {0, 1, 0, 0, 0, 6, 1, 3, 0, 0, 0, 10};

    // outputStream.write(request);
    // outputStream.flush();

    // byte[] inputBuffer = new byte[1024];
    // int receivedLength = inputStream.read(inputBuffer, 0, inputBuffer.length);
    // byte[] inputByte = Arrays.copyOfRange(inputBuffer, 0, receivedLength);

    // System.out.println(Arrays.toString(inputByte));

    // if (receivedLength > 0) {
    // System.out.println(Arrays.toString(addByte(inputByte)));
    // }
    // } catch (IOException e) {
    // System.err.println(e.getMessage() + " | 연결에 실패하였습니다.");
    // }
    // }
}
