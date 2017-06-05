package com.outstanding.redis;

import redis.clients.jedis.Jedis;

import java.sql.SQLOutput;
import java.util.Set;

/**
 * Created by songll on 2017/4/19.
 */
public class testJedis {
    //单实例连接redis
    public static void main1(String[] args) {
//        Jedis jedis = getJedis();
//        for (int i = 0; i < 100000; i++) {
//            long v = jedis.lpush("listTest", String.valueOf(i));
//            System.out.println(v);
//        }
//        jedis.quit();


//        for (int i = 0; i < 1000; i++) {
//            new Thread(
//                    new Runnable() {
//                        public void run() {
//
//                            Jedis jedis = RedisUtils.getJedis();
//                            for (int j = 0; j < 100; j++) {
//                                String val = jedis.rpop("listTest");
//                                System.out.println(val);
//                            }
//                            RedisUtils.returnResource(jedis);
//                        }
//                    }
//            ).start();
//        }


    }

    public static void main(String[] args) {
        Jedis jedis = getJedis();
//        for (int i = 0; i < 10; i++) {
////            long v = jedis.lpush("listTest", String.valueOf(i));
//            String v = jedis.set("testKey"+i,"testValue"+i);
//            System.out.println(v);
//        }

        //Set<String> setV = jedis.keys("*");
//            System.out.println(setV.size());
//        for (String v : setV) {
//            System.out.println(v);
//        }
        Long count = jedis.objectRefcount("test*");
        System.out.println(count);
        jedis.quit();
    }



    public static Jedis getJedis() {
        Jedis jedis = new Jedis("192.168.28.119", 6379);
        jedis.auth("123456");
        return jedis;
    }
}
