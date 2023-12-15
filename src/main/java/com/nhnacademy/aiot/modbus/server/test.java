package com.nhnacademy.aiot.modbus.server;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.core.jmx.Server;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class test {
    public static void main(String[] args) throws IOException {

        byte a = (byte)888;
        byte[] b = SimpleMB.intToByte(888); 
        System.out.println(b[0]);
        System.out.println(b[1]);
    }
}
