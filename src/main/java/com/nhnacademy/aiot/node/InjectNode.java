package com.nhnacademy.aiot.node;

import java.util.Random;
import org.json.JSONObject;
import com.nhnacademy.aiot.message.JsonMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InjectNode extends InputNode {
    Random random;
    int endAddress;
    int startValue;
    int endValue;

    public InjectNode(String name, int count) {
        super(name, count);
        setInterval(3000);
        this.random = new Random();
        this.endAddress = 10;
        this.startValue = 20;
        this.endValue = 30;
    }

    public void setRangeAddress(int endAddress) {
        this.endAddress = endAddress;
    }

    public void setRangeValue(int start, int end) {
        this.startValue = start;
        this.endValue = end;
    }

    @Override
    void process() {
        JSONObject modbusRequest = new JSONObject();
        int address = 104;
        double value = startValue + Math.random() * endValue;
        modbusRequest.put("value" , value);
        modbusRequest.put("address", address);
        modbusRequest.put("register", "register");
        log.info("{}", value);
        output(0, new JsonMessage(new JSONObject(modbusRequest.toString())));
    }
    
}
