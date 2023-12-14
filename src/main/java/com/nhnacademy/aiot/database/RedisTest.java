package com.nhnacademy.aiot.database;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTest {
    public static void main(String[] args) {

        String host = "127.0.0.1";
        int port = 6379;
        int timeout = 3000;
        int db = 0;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        JedisPool pool = new JedisPool(jedisPoolConfig, host, port, timeout, null, db);

        Jedis jedis = pool.getResource();
        for (int i = 1; i <= 23; i++) {
            jedis.hset("sensorInfo", String.valueOf(i), "0");
        }
    }

}
