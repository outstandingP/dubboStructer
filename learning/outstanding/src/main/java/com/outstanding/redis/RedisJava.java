package com.outstanding.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by songll on 2017/3/30.
 */
public class RedisJava {
    public static void main(String[] args) {

        //连接本地的 Redis 服务
//        Jedis jedis = new Jedis("192.168.16.70");
        //Jedis jedis = new Jedis("192.168.16.70",6379);
        //Jedis jedis = new Jedis("192.168.28.119",6379);
        Jedis jedis = new Jedis("172.17.20.12",6379);
        //jedis.auth("123456");
        System.out.println("Connection to server sucessfully");
//        //查看服务是否运行
        System.out.println("Server is running: " + jedis.ping());

//        //设置 redis 字符串数据
//        jedis.set("runoobkey", "Redis tutorial");
//        // 获取存储的数据并输出
//        System.out.println("Stored string in redis:: "+ jedis.get("runoobkey"));
//
//        //存储数据到列表中
//        jedis.lpush("tutorial-list", "Redis");
//        jedis.lpush("tutorial-list", "Mongodb");
//        jedis.lpush("tutorial-list", "Mysql");
//        // 获取存储的数据并输出
//        List<String> list = jedis.lrange("tutorial-list", 0 ,5);
//        for(int i=0; i<list.size(); i++) {
//            System.out.println("Stored string in redis:: "+list.get(i));
//        }
//
//        // 获取数据并输出
//        Set<String> set = jedis.keys("*");
//        Iterator<String> it = set.iterator();
//        while (it.hasNext()) {
//            System.out.println("List of stored keys:: " + it.next());
//        }
    }
}
