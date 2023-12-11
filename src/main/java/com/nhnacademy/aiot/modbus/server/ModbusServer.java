package com.nhnacademy.aiot.modbus.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModbusServer implements Runnable {
    private int[] holdingRegisters;
    private int[] inputRegisters;
    private final ServerSocket serverSocket;
    private final Thread thread;

    public ModbusServer(ServerSocket serverSocket, int[] holdingRegisters, int[] inputRegisters) {
        this.serverSocket = serverSocket;
        this.holdingRegisters = holdingRegisters;
        this.inputRegisters = inputRegisters;
        this.thread = new Thread(this, this.getClass().getSimpleName());
    }

    public void start() {
        thread.start();
    }


    @Override
    public void run() {

        try {
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                HandlerServer handlerServer =
                        new HandlerServer(socket, holdingRegisters, inputRegisters);
                handlerServer.start();

                log.info("{} | PORT : {}", socket.getInetAddress().getHostAddress(),
                        socket.getPort());
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}


