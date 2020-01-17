package com.ant;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws Exception{
        HashMap<String,String> hashMap = new HashMap<String, String>(1);
        hashMap.put("hah","xcl");
        hashMap.put("sdf","fda");
        Jedis jedis = new Jedis();
        jedis.watch("age");
        Transaction transaction = jedis.multi();
        TimeUnit.SECONDS.sleep(10);
        Response<Long> age = transaction.incr("age");
        List<Object> exec = transaction.exec();
        System.out.println(age.get());
    }
}
