package com.nhnacademy.aiot.gateway;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.nhnacademy.aiot.modbus.client.Client;
import com.nhnacademy.aiot.node.ActiveNode;
import com.nhnacademy.aiot.node.ModbusReadNode;
import com.nhnacademy.aiot.node.ModbusServerNode;
import com.nhnacademy.aiot.node.ModbusWriteNode;
import com.nhnacademy.aiot.node.PlaceTranslatorNode;
import com.nhnacademy.aiot.node.SplitNode;
import com.nhnacademy.aiot.wire.BufferedWire;
import com.nhnacademy.aiot.wire.Wire;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SettingNode {
    protected static String settingPath =
            "src/main/java/com/nhnacademy/aiot/setting/nodeSetting.json";
    // protected static String settingPath =
    // "src/main/java/com/nhnacademy/aiot/setting/modbusSetting.json";
    protected static String path = "com.nhnacademy.aiot.node.";
    protected static String clientPath = "com.nhnacademy.aiot.modbus.client.";
    private HashMap<String, ActiveNode> nodeList;
    private HashMap<Integer, List<String>> wireput;
    private HashMap<String, Map<Integer, List<String>>> wireMap;
    private HashMap<String, Client> clientMap;
    private Object object;

    private String aplicationName = "#";
    private List<String> sensors;

    private boolean checkcommand;

    public SettingNode() {
        this.nodeList = new HashMap<>();
        this.wireMap = new HashMap<>();
        this.wireput = new HashMap<>();
        this.clientMap = new HashMap<>();

        try {
            this.object = new JSONParser().parse(new FileReader(settingPath));
        } catch (IOException | ParseException e) {

        }
    }

    public void commandLineOn() {
        this.checkcommand = true;
    }

    public boolean isCommandLine() {
        return this.checkcommand;
    }

    public void setAplicationName(String aplicationName) {
        this.aplicationName = aplicationName;
    }

    public void setSensors(List<String> sensors) {
        this.sensors = sensors;
    }

    private void makeNode(JSONObject node) {
        try {
            String nodeType = (String) node.get("type");
            String nodeId = (String) node.get("id");
            Class<?> nodeClass = Class.forName(path + nodeType);
            Constructor<?> nodeConstructor = nodeClass.getConstructor(String.class, int.class);
            JSONArray wireInfo = null;
            int wireSize = 0;
            if (Objects.nonNull(node.get("wire"))) {
                wireInfo = (JSONArray) node.get("wire");
                wireSize = wireInfo.size();
            }
            Object newObj = nodeConstructor.newInstance(nodeId, wireSize < 1 ? 1 : wireSize);
            int wirePort = 0;
            if (Objects.nonNull(wireInfo) && !wireInfo.isEmpty()) {
                wireput = new HashMap<>();
                for (Object w : wireInfo) {
                    List<String> wireOutList = new ArrayList<>();
                    JSONArray wireArray = (JSONArray) w;
                    for (Object connectWire : wireArray) {
                        wireOutList.add((String) connectWire);
                    }
                    wireput.put(wirePort, wireOutList);
                    wirePort++;
                }
                wireMap.put(nodeId, wireput);
            }
            if (newObj instanceof ModbusReadNode || newObj instanceof ModbusWriteNode) {
                Method setDataType = newObj.getClass().getMethod("setDataType", String.class);
                Method setQuantity = newObj.getClass().getMethod("setQuantity", int.class);
                Method setServerName = newObj.getClass().getMethod("setServerName", String.class);
                Method setAddress = newObj.getClass().getMethod("setAddress", int.class);

                String dataType = (String) node.get("dataType");
                int quantity = Integer.parseInt(node.get("quantity").toString());
                String serverName = (String) node.get("server");
                int address = Integer.parseInt(node.get("adr").toString());

                setDataType.invoke(newObj, dataType);
                setQuantity.invoke(newObj, quantity);
                setServerName.invoke(newObj, serverName);
                setAddress.invoke(newObj, address);

            } else if (newObj instanceof ModbusServerNode) {
                Method setHoldingRegisters =
                        newObj.getClass().getMethod("setHoldingRegisters", int.class);
                Method setInputRegisters =
                        newObj.getClass().getMethod("setInputRegisters", int.class);
                Method setServerPort = newObj.getClass().getMethod("setServerPort", int.class);

                int holdingBufferSize = Integer.parseInt(node.get("holdingBufferSize").toString());
                int inputBufferSize = Integer.parseInt(node.get("inputBufferSize").toString());
                int serverPort = Integer.parseInt(node.get("serverPort").toString());

                setHoldingRegisters.invoke(newObj, holdingBufferSize);
                setInputRegisters.invoke(newObj, inputBufferSize);
                setServerPort.invoke(newObj, serverPort);
            }
            nodeList.put(nodeId, (ActiveNode) newObj);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            log.error("error-", e);
        }

    }

    private void makeClient(JSONObject node) {
        try {
            String clientType = (String) node.get("type");
            String clientId = (String) node.get("id");
            String clientName = (String) node.get("name");
            String host = (String) node.get("tcpHost");
            int port = Integer.parseInt(node.get("tcpPort").toString());
            int unitId = Integer.parseInt(node.get("unit_id").toString());
            Class<?> clientClass = Class.forName(clientPath + clientType);
            Constructor<?> clientConstructor =
                    clientClass.getConstructor(String.class, String.class, int.class, int.class);
            Object client = clientConstructor.newInstance(clientName, host, port, unitId);
            clientMap.put(clientId, (Client) client);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void makeFlow() {
        JSONArray flowJson = ((JSONArray) object);
        for (Object node : (JSONArray) flowJson.get(0)) {
            String nodeType = (String) ((JSONObject) node).get("type");
            if (nodeType.contains("Node")) {
                makeNode((JSONObject) node);
            } else {
                makeClient((JSONObject) node);
            }
        }
    }

    public void connectWire() {
        try {
            for (String input : wireMap.keySet()) {
                Map<Integer, List<String>> getWireMap = wireMap.get(input);
                for (int portNumber : getWireMap.keySet()) {
                    for (String connectNodeName : getWireMap.get(portNumber)) {
                        Wire wire = new BufferedWire();
                        ActiveNode inputNode = nodeList.get(input);
                        ActiveNode outputNode = nodeList.get(connectNodeName);
                        System.out.println(outputNode.getClass().getSimpleName());
                        Method connectOutputWire = inputNode.getClass()
                                .getMethod("connectOutputWire", int.class, Wire.class);
                        Method connectInputWire = outputNode.getClass()
                                .getMethod("connectInputWire", int.class, Wire.class);
                        connectOutputWire.invoke(inputNode, portNumber, wire);
                        connectInputWire.invoke(outputNode, 0, wire);

                        System.out.println(inputNode.getClass().getSimpleName() + ">>>>"
                                + portNumber + ">>>>" + outputNode.getClass().getSimpleName());
                    }

                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            log.error("error-", e);
        }
    }

    public void nodeStart() {
        for (ActiveNode node : nodeList.values()) {
            if (node instanceof SplitNode) {
                splitNodeSetup(((SplitNode) node));
            } else if (node instanceof PlaceTranslatorNode) {
                ((PlaceTranslatorNode) node).setPlaceInfo(new HashMap<>(Map.of("class_a", "강의실 A",
                        "class_b", "강의실 B", "server_room", "서버실", "lobby", "로비", "office", "사무실",
                        "storage", "창고", "meeting_room", "미팅룸", "pair_room", "페어룸", "냉장고", "냉장고")));
            } else if (node instanceof ModbusServerNode) {
                ((ModbusServerNode) node).launchServer();
            } else if (node instanceof ModbusReadNode || node instanceof ModbusWriteNode) {
                try {
                    Method setServer = node.getClass().getMethod("setServer", Client.class);
                    Method getServerName = node.getClass().getMethod("getServerName");
                    String serverName = (String) getServerName.invoke(node);
                    setServer.invoke(node, clientMap.get(serverName));

                } catch (NoSuchMethodException | IllegalAccessException
                        | InvocationTargetException e) {
                    System.err.println(e.getMessage());
                }
            }
            node.start();
        }
    }

    public void splitNodeSetup(SplitNode node) {
        if (isCommandLine()) {
            node.setAplicationName(aplicationName);
            node.setSensors(sensors);
            return;
        }
        setSplitOption(node);
    }

    private void setSplitOption(SplitNode node) {
        JSONArray flowJson = ((JSONArray) object);
        for (Object setting : (JSONArray) flowJson.get(1)) {
            this.aplicationName = (String) ((JSONObject) setting).get("--an");

            this.sensors = new ArrayList<>(
                    List.of(((String) ((JSONObject) setting).get("-s")).split(",")));
            node.setAplicationName(aplicationName);
            node.setSensors(sensors);
        }
    }

    public void checkCommandLine(String[] args) {
        Options commandOptions = new Options();
        commandOptions.addOption("c", null, false, "이 옵션이 주어질 경우 commandLine으로 세팅한다");
        commandOptions.addOption(null, "an", true, "application name이 주어질 경우 해당 메시지만 수신하도록 한다.");
        commandOptions.addOption("s", null, true, "해당하는 센서만 사용 ,로 구분한다");
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;

        try {
            commandLine = parser.parse(commandOptions, args);

            if (commandLine.hasOption("c")) {
                commandLineOn();
            }
            if (commandLine.hasOption("an")) {
                setAplicationName(commandLine.getOptionValue("an"));
            }
            if (commandLine.hasOption("s")) {
                setSensors(new ArrayList<>(List.of(commandLine.getOptionValue("s").split(","))));
            }
        } catch (org.apache.commons.cli.ParseException e) {
            System.err.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Help", commandOptions);
        }

    }

}

