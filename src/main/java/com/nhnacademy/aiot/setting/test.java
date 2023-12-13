package com.nhnacademy.aiot.setting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {
    
    public static void main(String[] args) {
        
        Map<String, Map<Integer,List<String>>> totalMap = new HashMap<>();
        Map<Integer,List<String>> innerMap = new HashMap<>();
        innerMap.put(0, List.of("abc", "def"));
        innerMap.put(1, List.of("zzz"));
        System.err.println(innerMap);

        totalMap.put("id", innerMap);
        System.out.println(totalMap);

        
    }
}
