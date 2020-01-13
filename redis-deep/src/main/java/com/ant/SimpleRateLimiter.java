package com.ant;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.io.IOException;

/**
 * 使用zset模拟时间滑动窗口来限制某一个用户的某一个操作在时间周期范围内只允许操作N次
 */
public class SimpleRateLimiter {
    private final Jedis jedis;

    public SimpleRateLimiter(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     *
     * @param userId 用户id
     * @param actionKey 操作
     * @param period 时间周期
     * @param maxCount 能操作的最大数量
     * @return
     */
    public boolean isActionAllowed(String userId,String actionKey,int period,int maxCount) throws IOException {
        //拼接不允许重复的key，即标识具体用户的具体操作
        String key = String.format("hist:%s:%s",userId,actionKey);
        long now = System.currentTimeMillis();
        Pipeline pipeline = jedis.pipelined();
        //使用pipeline.multi()保证使用pipeline执行的多个命令操作是原子的，类似于事务
        pipeline.multi();
        pipeline.zadd(key,now,now+"");
        //将时间周期之前的数据删除
        pipeline.zremrangeByScore(key,0,now-period*1000);
        Response<Long> zcard = pipeline.zcard(key);
        pipeline.expire(key,period+1);
        pipeline.exec();
        pipeline.close();
        return zcard.get() <= maxCount;
    }

    public static void main(String[] args) throws Exception{
        Jedis jedis = new Jedis();
        SimpleRateLimiter simpleRateLimiter = new SimpleRateLimiter(jedis);
        for(int i=0;i<20;i++){
            PU.println(simpleRateLimiter.isActionAllowed("ant","addObject",60,5));
        }
    }
}
