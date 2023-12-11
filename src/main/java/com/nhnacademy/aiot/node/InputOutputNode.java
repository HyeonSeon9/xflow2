package com.nhnacademy.aiot.node;


import com.nhnacademy.aiot.exception.OutOfBoundsException;
import com.nhnacademy.aiot.message.ByteMessage;
import com.nhnacademy.aiot.message.JsonMessage;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.wire.Wire;

public abstract class InputOutputNode extends ActiveNode {
    Wire[] inputWires;
    Wire[] outputWires;

    InputOutputNode(String name, int inCount, int outCount) {
        super(name);

        inputWires = new Wire[inCount];
        outputWires = new Wire[outCount];
    }

    InputOutputNode(int inCount, int outCount) {
        super();

        inputWires = new Wire[inCount];
        outputWires = new Wire[outCount];
    }

    public void connectOutputWire(int index, Wire wire) {
        if (index < 0 || outputWires.length <= index) {
            throw new OutOfBoundsException();
        }

        outputWires[index] = wire;
    }

    public int getOutputWireCount() {
        return outputWires.length;
    }

    public Wire getOutputWire(int index) {
        if (index < 0 || outputWires.length <= index) {
            throw new OutOfBoundsException();
        }

        return outputWires[index];
    }

    public void connectInputWire(int index, Wire wire) {
        if (index < 0 || inputWires.length <= index) {
            throw new OutOfBoundsException();
        }

        inputWires[index] = wire;
    }

    public int getInputWireCount() {
        return inputWires.length;
    }

    public Wire getInputWire(int index) {
        if (index < 0 || inputWires.length <= index) {
            throw new OutOfBoundsException();
        }

        return inputWires[index];
    }

    void output(Message message) {
        log.trace("Message Out");
        for (Wire port : outputWires) {
            Message putMessage = null;
            if (message instanceof JsonMessage) {
                putMessage = new JsonMessage(((JsonMessage) message).getPayload());
            } else {
                putMessage = new ByteMessage(((ByteMessage) message).getPayload());
            }
            if (port != null) {
                port.put(putMessage);
            }
        }
    }
}

