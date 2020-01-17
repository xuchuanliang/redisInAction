package com.ant;

import java.util.HashMap;
import java.util.Map;

/**
 * 漏斗算法：
 * 漏斗算法的本质是由一个容器，容器的容量是固定的，该容器中的数据随着时间的流程以一个固定的频率减少，
 * 向容器中增加数据需要保证容器中有足够的空间
 */
public class RunnelRateLimiter {
    static class Funnel{
        //漏斗容量
        int capacity;
        //漏斗流动速率
        float leakingRate;
        //漏斗中能够放置的容量
        int leftQuota;
        //时间
        long leakingTs;
        public Funnel(int capacity,float leakingRate){
            this.capacity = capacity;
            this.leakingRate = leakingRate;
            this.leftQuota = capacity;
            this.leakingTs = System.currentTimeMillis();
        }
        void makeSpace(){
            long nowTs = System.currentTimeMillis();
            long deltaTs = nowTs - leakingTs;
            int deltaQuota = (int)(deltaTs * leakingRate);
            //间隔时间太长，整数数字过大溢出
            if(deltaQuota < 0){
                this.leftQuota = capacity;
                this.leakingTs = nowTs;
                return;
            }
            //腾出空间太小，最小单位是1
            if(deltaQuota < 1){
                return;
            }
            this.leftQuota += deltaQuota;
            this.leakingTs = nowTs;
            if(this.leftQuota > this.capacity){
                this.leftQuota = this.capacity;
            }
        }
        boolean watering(int quota){
            makeSpace();
            if(this.leftQuota >= quota){
                this.leftQuota -= quota;
                return true;
            }
            return false;
        }
    }
    private Map<String,Funnel> funnelMap = new HashMap<String, Funnel>(256);

    public boolean isActionAllowed(String userId,String actionKey,int capacity,float leakingRate){
        String key = String.format("%s:%s",userId,actionKey);
        Funnel funnel = funnelMap.get(key);
        if(funnel == null){
            funnel = new Funnel(capacity,leakingRate);
            funnelMap.put(key,funnel);
        }
        //需要一个quota
        return funnel.watering(1);
    }
}
