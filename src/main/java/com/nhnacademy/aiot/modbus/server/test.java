package com.nhnacademy.aiot.modbus.server;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class test {
    public static void main(String[] args) throws IOException {

        try {
            Object object = new JSONParser().parse(new FileReader(
                    "/home/nhnacademy/mini-project-aiot-gateway1/src/main/java/com/nhnacademy/aiot/setting/nodeSetting.json"));
            JSONArray jsonArray = (JSONArray) object;
            JSONArray jsonArray2 = (JSONArray) jsonArray.get(0);
            for (Object o : jsonArray2) {
                JSONObject jsonObject = (JSONObject) o;
                Set<Map.Entry<Object, Object>> set = jsonObject.entrySet();
                for (Map.Entry<Object, Object> map : set) {
                    System.out.println(map.getKey() + " : " + map.getValue());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
