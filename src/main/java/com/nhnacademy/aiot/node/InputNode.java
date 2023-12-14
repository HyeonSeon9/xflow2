package com.nhnacademy.aiot.node;

import java.util.ArrayList;
import java.util.List;
import com.nhnacademy.aiot.exception.InvalidArgumentException;
import com.nhnacademy.aiot.exception.OutOfBoundsException;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.wire.Wire;

public abstract class InputNode extends ActiveNode {
    List<List<Wire>> outputWires;

    InputNode(String name) {
        this(name, 1);
    }

    InputNode(String name, int count) {
        super(name);

        if (count <= 0) {
            throw new InvalidArgumentException();
        }

        outputWires = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            outputWires.add(new ArrayList<>());
        }
    }

    InputNode(int count) {
        super();

        if (count <= 0) {
            throw new InvalidArgumentException();
        }

        outputWires = new ArrayList<>();
        for (int i = 0; i < count; i++) {
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
