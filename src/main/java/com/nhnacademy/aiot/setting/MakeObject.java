package com.nhnacademy.aiot.setting;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.nhnacademy.aiot.modbus.client.Broker;
import com.nhnacademy.aiot.modbus.client.Client;
import com.nhnacademy.aiot.node.ActiveNode;
import com.nhnacademy.aiot.node.ModbusReadNode;
import com.nhnacademy.aiot.node.ModbusServerNode;
import com.nhnacademy.aiot.node.ModbusWriteNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MakeObject {
    protected static String path = "com.nhnacademy.aiot.node.";
    protected static String clientPath = "com.nhnacademy.aiot.modbus.client.";

    private HashMap<String, ActiveNode> nodeList;
    private HashMap<Integer, List<String>> wireput;
    private HashMap<String, Map<Integer, List<String>>> wireMap;
    private HashMap<String, Client> clientMap;
    private HashMap<String, Broker> brokerMap;

    public MakeObject() {
        this.nodeList = new HashMap<>();
        this.wireMap = new HashMap<>();
        this.wireput = new HashMap<>();
        this.clientMap = new HashMap<>();
        this.brokerMap = new HashMap<>();
    }

    public void makeNode(JSONObject node) {
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

            if (Objects.nonNull(node.get("broker"))) {
                Method setBrokerName = newObj.getClass().getMethod("setBrokerName", String.class);
                String brokerName = (String) node.get("broker");
                setBrokerName.invoke(newObj, brokerName);
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

    public void makeClient(JSONObject object) {
        try {
            String clientType = (String) object.get("type");
            String clientId = (String) object.get("id");
            String clientName = (String) object.get("name");
            String host = (String) object.get("tcpHost");
            int port = Integer.parseInt(object.get("tcpPort").toString());
            int unitId = Integer.parseInt(object.get("unit_id").toString());
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

    public void makeBroker(JSONObject object) {
        try {
            String brokerType = (String) object.get("type");
            String brokerId = (String) object.get("id");
            String brokerName = (String) object.get("name");
            String brokerHost = (String) object.get("broker");

            int port = Integer.parseInt(object.get("port").toString());
            int keepAlive = Integer.parseInt(object.get("keepalive").toString());

            boolean autoConnect = Boolean.parseBoolean(object.get("autoConnect").toString());
            boolean cleanSession = Boolean.parseBoolean(object.get("cleansession").toString());

            Class<?> brokerClass = Class.forName(clientPath + brokerType);
            Constructor<?> brokerConstructor = brokerClass.getConstructor(String.class,
                    String.class, int.class, int.class, boolean.class, boolean.class);
            Object broker = brokerConstructor.newInstance(brokerName, brokerHost, port, keepAlive,
                    autoConnect, cleanSession);

            brokerMap.put(brokerId, (Broker) broker);
            ((Broker) broker).connect();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public Map<String, ActiveNode> getNodeList() {
        return nodeList;
    }

    public Map<Integer, List<String>> getWireput() {
        return wireput;
    }

    public Map<String, Map<Integer, List<String>>> getWireMap() {
        return wireMap;
    }

    public Map<String, Client> getClientMap() {
        return clientMap;
    }

    public Map<String, Broker> getBrokerMap() {
        return brokerMap;
    }
}
