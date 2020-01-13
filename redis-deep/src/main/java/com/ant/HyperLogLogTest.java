package com.ant;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.UUID;

public class HyperLogLogTest {
    private static final String key = "UV";
    private static final Jedis jedis = new Jedis();
    public static void main(String[] args) {
        testHyperLogLog();
        testHyperLogLogPipeline();
        testSet();
        testSetPipeline();
    }
    private static void testHyperLogLog(){
        jedis.del(key);
        long start = System.currentTimeMillis();
        for(int i=0;i<1000000;i++){
            jedis.pfadd(key,UUID.randomUUID().toString());
        }
        long end = System.currentTimeMillis();
        PU.println(end-start);
        PU.println(jedis.pfcount(key));
        PU.println("=================================");
    }

    private static void testHyperLogLogPipeline(){
        jedis.del(key);
        long start = System.currentTimeMillis();
        Pipeline pipelined = jedis.pipelined();
        for(int i=0;i<1000000;i++){
            pipelined.pfadd(key,UUID.randomUUID().toString());
        }
        pipelined.sync();
        long end = System.currentTimeMillis();
        PU.println(end-start);
        PU.println(jedis.pfcount(key));
        PU.println("=================================");
    }

    private static void testSet(){
        jedis.del(key);
        long start = System.currentTimeMillis();
        for(int i=0;i<1000000;i++){
            jedis.sadd(key,UUID.randomUUID().toString());
        }
        long end = System.currentTimeMillis();
        PU.println(end-start);
        PU.println(jedis.scard(key));
        PU.println("=================================");
    }

    private static void testSetPipeline(){
        jedis.del(key);
        long start = System.currentTimeMillis();
        Pipeline pipelined = jedis.pipelined();
        for(int i=0;i<1000000;i++){
            pipelined.sadd(key,UUID.randomUUID().toString());
        }
        pipelined.sync();
        long end = System.currentTimeMillis();
        PU.println(end-start);
        PU.println(jedis.scard(key));
        PU.println("=================================");
    }
}
