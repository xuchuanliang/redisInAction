package com.ant;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis事务demo
 */
public class TransactionDemo {
    public static void main(String[] args) throws Exception{
        Jedis jedis = new Jedis();
        String userId = "ant";
        String key = String.format("account_{}",userId);
        PU.println(key);
        jedis.setnx(key,String.valueOf(6));
        int val = doubleAccount(jedis,key);
        PU.println(val);

    }
    public static int doubleAccount(Jedis jedis,String key)throws Exception{
        while (true){
            jedis.watch(key);
            TimeUnit.SECONDS.sleep(5);
            int value = Integer.parseInt(jedis.get(key));
            value *= 2;
            Transaction transaction = jedis.multi();
            transaction.set(key,String.valueOf(value));
            List<Object> exec = transaction.exec();
            if(exec!=null){
                //成功
                break;
            }
        }
        return Integer.parseInt(jedis.get(key));
    }
}
