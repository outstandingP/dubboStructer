package com.outstanding.redis.cnblogs;
import java.util.List;

import redis.clients.jedis.Jedis;
/**
 * 类名: RedisListJava </br>
 * 包名： com.souvc.redis
 * 描述: Redis Java List(列表) 实例  </br>
 * 开发人员： souvc  </br
 * 创建时间：  2015-12-9 </br>
 * 发布版本：V1.0  </br>
 */
public class RedisListJava {
    public static void main(String[] args) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis("192.168.16.70");
        System.out.println("连接本地的 Redis 服务成功！");

        // 存储数据到列表中
        jedis.del("kecheng");
        jedis.rpush("kecheng", "java");
        jedis.rpush("kecheng", "php");
        jedis.rpush("kecheng", "Mysql");

        // 获取存储的数据并输出
        List<String> list = jedis.lrange("kecheng", 0, 5);
        for (int i = 0; i < list.size(); i++) {
            System.out.println("redis list里面存储的值是:" + list.get(i));
        }

    }
}
