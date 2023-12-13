package com.nhnacademy.aiot.databases;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis {
    public static void main(String[] args) {
        JedisPoolConfig config = new JedisPoolConfig();
        JedisPool pool = new JedisPool(config, "localhost", 11502, 1000, "1234");
        Jedis jedis = pool.getResource();


    }
}
