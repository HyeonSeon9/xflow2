package com.nhnacademy.aiot.modbus.server;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisServer {
    protected static final String HOST = "localhost";
    protected static final int PORT = 6379;
    protected static final int DB = 0;
    JedisPoolConfig jedisPoolConfig;
    JedisPool pool;
    Jedis jedis;

    public RedisServer() {

    }

    public void connect() {
        this.jedisPoolConfig = new JedisPoolConfig();
        this.pool = new JedisPool(jedisPoolConfig, HOST, PORT, 3000, null, DB);
        this.jedis = pool.getResource();
    }

    public Jedis getJedis() {
        return jedis;
    }

}
