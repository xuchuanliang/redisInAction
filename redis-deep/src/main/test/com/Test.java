package com;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;

import java.util.Currency;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Test {

    @org.junit.Test
    public void addBatch(){
        Jedis jedis = new Jedis("192.168.109.130",6379);
        for(int i=0;i<100;i++){
            Pipeline pipelined = jedis.pipelined();
            pipelined.multi();
            for(int j=0;j<10000;j++){
                pipelined.set("key"+ UUID.randomUUID().toString(),"value"+UUID.randomUUID().toString());
            }
            pipelined.exec();
        }
        System.out.println("end");
    }

    @org.junit.Test
    public void testCluster(){
        Set<HostAndPort> set = new HashSet<>(6);
        set.add(new HostAndPort("192.168.109.130",6379));
        set.add(new HostAndPort("192.168.109.131",6379));
        set.add(new HostAndPort("192.168.109.132",6379));
        set.add(new HostAndPort("192.168.109.133",6379));
        set.add(new HostAndPort("192.168.109.134",6379));
        set.add(new HostAndPort("192.168.109.135",6379));
        JedisCluster cluster = new JedisCluster(set);
        String lala = cluster.get("lala");
        System.out.println(lala);
    }
}
