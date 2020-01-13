package com.ant;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * redis实现可重入锁
 */
public class RedisWithReentrantLock {
    private final ThreadLocal<Map<String,Integer>> threadLocal = new ThreadLocal<Map<String, Integer>>();
    private final Jedis jedis;

    public RedisWithReentrantLock(Jedis jedis) {
        this.jedis = jedis;
    }

    private boolean _lock(String key,String value,int expireTime){
        String set = jedis.set(key, value, "nx", "ex",expireTime);
        return null!=set;
    }

    private void _unLock(String key){
        jedis.del(key);
    }

    /**
     * 获取当前线程锁map
     * @return
     */
    private Map<String,Integer> currentLock(){
        Map<String,Integer> map = threadLocal.get();
        if(null != map){
            return map;
        }
        threadLocal.set(new HashMap<String, Integer>(256));
        return threadLocal.get();
    }

    /**
     * 加锁操作，兼容可重入，即同一个线程可重复加锁
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public boolean lock(String key,String value,int expireTime){
        Map<String, Integer> map = currentLock();
        Integer integer = map.get(key);
        if(null!=integer){
            //当前线程对该key已经加锁，重入即可
            map.put(key,integer+1);
            return true;
        }
        if(_lock(key,value,expireTime)){
            map.put(key,1);
            return true;
        }
        return false;
    }

    /**
     * 释放锁操作，同一个线程重入情况下释放则减少
     * @param key
     * @return
     */
    public boolean unlock(String key){
        Map<String, Integer> map = threadLocal.get();
        Integer integer = map.get(key);
        if(integer == null){
            return false;
        }
        integer -=1;
        if(integer == 0){
            map.remove(key);
            this._unLock(key);
            return true;
        }else{
            map.put(key,integer);
            return true;
        }
    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        RedisWithReentrantLock lock = new RedisWithReentrantLock(jedis);
        System.out.println(lock.lock("name","xuchuanliang",10));
        PU.println(lock.lock("name","xuchuanliang",100));
        PU.println(lock.unlock("name"));
        PU.println(lock.unlock("name"));
    }
}
