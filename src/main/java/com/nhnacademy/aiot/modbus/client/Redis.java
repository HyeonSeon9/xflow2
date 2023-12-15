package com.nhnacademy.aiot.modbus.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {
    JedisPoolConfig jedisPoolConfig;
    JedisPool pool;
    Jedis jedis;
    private String name;
    private String host;
    private int port;
    private int db;
    private int timeout;


    public Redis(String name, String host, int port, int db, int timeout) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.db = db;
        this.timeout = timeout;
    }

    public void connect() {
        this.jedisPoolConfig = new JedisPoolConfig();
        this.pool = new JedisPool(jedisPoolConfig, host, port, timeout, null, db);
        this.jedis = pool.getResource();
    }

    public void hsetPut(String key, String recordKey, String recordValue) {
        jedis.hset(key, recordKey, recordValue);
    }

}
