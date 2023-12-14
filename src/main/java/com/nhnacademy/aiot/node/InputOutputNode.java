package com.nhnacademy.aiot.node;


import java.util.ArrayList;
import java.util.List;
import com.nhnacademy.aiot.exception.OutOfBoundsException;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.wire.Wire;

public abstract class InputOutputNode extends ActiveNode {
    List<Wire> inputWires;
    int inCount;
    List<List<Wire>> outputWires;

    InputOutputNode(String name, int inCount, int outCount) {
        super(name);
        this.inCount = inCount;
        inputWires = new ArrayList<>();
        outputWires = new ArrayList<>();
        for (int i = 0; i < outCount; i++) {
            outputWires.add(new ArrayList<>());
        }
    }

    InputOutputNode(int inCount, int outCount) {
        super();
        this.inCount = inCount;
        inputWires = new ArrayList<>();
        outputWires = new ArrayList<>();
        for (int i = 0; i < outCount; i++) {
            outputWires.add(new ArrayList<>());
        }
    }

    public void connectOutputWire(int port, Wire wire) {
        if (outputWires.size() <= port) {
            throw new OutOfBoundsException();
        }


        outputWires.get(port).add(wire);
    }

    public int getOutputWireCount() {
        return outputWires.size();
    }

    public Wire getoutputWire(int port, int index) {
        if (port < 0 || outputWires.size() <= port || outputWires.get(port).size() <= index) {
            throw new OutOfBoundsException();
        }

        return outputWires.get(port).get(index);
    }

    public void connectInputWire(int index, Wire wire) {
        if (inCount <= index) {
            throw new OutOfBoundsException();
        }
        inputWires.add(wire);
    }

    public int getInputWireCount() {
        return inputWires.size();
    }

    public Wire getInputWire(int index) {
        if (index < 0 || inputWires.size() <= index) {
            throw new OutOfBoundsException();
        }

        return inputWires.get(index);
    }

    void output(int port, Message message) {
        log.trace("Message Out");
        for (int i = 0; i < outputWires.get(port).size(); i++) {
            Message putMessage = null;
            Wire wire = outputWires.get(port).get(i);
            if (message instanceof JsonMessage) {
                putMessage = new JsonMessage(((JsonMessage) message).getPayload());
            } else {
                putMessage = new ByteMessage(((ByteMessage) message).getPayload());
            }
            if (wire != null) {
                wire.put(putMessage);
            }
        }
    }
}

