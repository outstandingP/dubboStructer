package com.outstanding.redis.cnblogs;

import redis.clients.jedis.Jedis;
/**
 * Created by songll on 2017/4/1.
 */
public class RedisSortJava {
    /**
     * 方法名：main</br>
     * 详述：jedis 排序   </br>
     * 开发人员：souvc </br>
     * 创建时间：2015-12-10  </br>
     * @param args 说明返回值含义
     */
    public static void main(String[] args) {

        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接本地的 Redis 服务成功！");

        //jedis 排序
        //注意，此处的rpush和lpush是List的操作。是一个双向链表（但从表现来看的）
        jedis.del("a");//先清除数据，再加入数据进行测试
        jedis.rpush("a", "1");
        jedis.lpush("a","6");
        jedis.lpush("a","3");
        jedis.lpush("a","9");
        System.out.println(jedis.lrange("a",0,-1));// [9, 3, 6, 1]
        System.out.println(jedis.sort("a")); //[1, 3, 6, 9]  //输入排序后结果
        System.out.println(jedis.lrange("a",0,-1));
    }
}
