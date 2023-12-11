package com.nhnacademy.aiot.modbus.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TcpSocket {
    private static final String HOST = "172.19.0.11";
    private static final int PORT = 502;

    public static void main(String[] args) {
        short k = 32767;
        System.out.println(k);
        try (Socket socket = new Socket(HOST, PORT);
                BufferedOutputStream socketOut = new BufferedOutputStream(socket.getOutputStream());
                BufferedInputStream socketIn = new BufferedInputStream(socket.getInputStream());) {
            byte[] bufferOut = new byte[] {0, 1, 0, 0, 0, 6, 1, 3, 0, 0, 0, 5};

            int unitId = 1;
            int transactionId = 0;
            // for (int i = 0; i < 5; i++) {
            // byte[] request = SimpleMB.addMBAP(++transactionId, unitId,
            // SimpleMB.makeReadHoldingRegistersRequest(0, 5));
            // socketOut.write(request);
            // socketOut.flush();
            // byte[] bufferIn = new byte[512];
            // int bufferInSize = socketIn.read(bufferIn, 0, bufferIn.length);
            // byte[] response = Arrays.copyOfRange(bufferIn, 0, bufferInSize);
            // System.out.println(Arrays.toString(response));
            // ArrayList<Integer> list = new ArrayList<>();
            // for (int j = 0; j < 10; j += 2) {
            // list.add((response[9 + j] + (response[9 + j + 1] << 8)));
            // }
            // for (int j : list) {
            // System.out.println(j);
            // }
            // }



        } catch (IOException e) {
            System.out.println("연결에 실패하였습니다.");
        }
    }

}
