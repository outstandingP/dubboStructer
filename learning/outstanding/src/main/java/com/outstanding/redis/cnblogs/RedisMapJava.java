package com.outstanding.redis.cnblogs;

import java.util.*;

import redis.clients.jedis.Jedis;

public class RedisMapJava {

    /**
     * 方法名：main</br>
     * 详述：redis操作Map </br>
     * 开发人员：souvc </br>
     * 创建时间：2015-12-10  </br>
     * @param args 说明返回值含义
     * @throws
     */
    public static void main(String[] args) {

        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis("192.168.16.70");
        System.out.println("连接本地的 Redis 服务成功！");


        //-----添加数据----------
        for (int i =0; i<10; i++){

            Map<String, String> map = new HashMap<String, String>();
            map.put("name", "xinxin-"+i);
//            map.put("age", "22-"+i);
            map.put("age", "");
            map.put("qq", "123456-"+i);

            jedis.hmset("user"+i,map);
        }
//        Set<String> set = jedis.keys("user*");
//        Iterator<String> it = set.iterator();
//        while(it.hasNext()){
//            String keyStr = it.next();
//            System.out.println(keyStr);
//            jedis.del(keyStr);
//        }

        //取出user中的name，执行结果:[minxr]-->注意结果是一个泛型的List
        //第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数

        List<String> rsmap = jedis.hmget("user1", "name", "age", "qq");
//
        System.out.println(rsmap);

//        //删除map中的某个键值
//        jedis.hdel("user","age");
//        System.out.println(jedis.hmget("user", "age")); //因为删除了，所以返回的是null
//        System.out.println(jedis.hlen("user")); //返回key为user的键中存放的值的个数2
//        System.out.println(jedis.exists("user"));//是否存在key为user的记录 返回true
//        System.out.println(jedis.hkeys("user"));//返回map对象中的所有key
//        System.out.println(jedis.hvals("user"));//返回map对象中的所有value
//
//        Iterator<String> iter=jedis.hkeys("user").iterator();
//        while (iter.hasNext()){
//            String key = iter.next();
//            System.out.println(key+":"+jedis.hmget("user",key));
//        }
    }

    public static void main2(String[] args) {
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis("192.168.16.70");
        Long db = jedis.getDB();
        System.out.println("连接本地的 Redis 服务成功！"+db);
    }
}
