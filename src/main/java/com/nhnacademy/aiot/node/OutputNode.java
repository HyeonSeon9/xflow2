package com.nhnacademy.aiot.node;

import java.util.ArrayList;
import java.util.List;
import com.nhnacademy.aiot.exception.AlreadyExistsException;
import com.nhnacademy.aiot.exception.InvalidArgumentException;
import com.nhnacademy.aiot.exception.OutOfBoundsException;
import com.nhnacademy.aiot.wire.Wire;

public abstract class OutputNode extends ActiveNode {
    List<Wire> inputWires;
    int count;

    OutputNode(String name) {
        this(name, 1);
    }

    OutputNode(String name, int count) {
        super(name);
        if (count <= 0) {
            throw new InvalidArgumentException();
        }

        inputWires = new ArrayList<>();
    }

    OutputNode(int count) {
        super();
        if (count <= 0) {
            throw new InvalidArgumentException();
        }

        inputWires = new ArrayList<>();
    }

    public void connectInputWire(int index, Wire wire) {
        if (count <= index) {
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
}
