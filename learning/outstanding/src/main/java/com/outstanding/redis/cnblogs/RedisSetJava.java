package com.outstanding.redis.cnblogs;
import redis.clients.jedis.Jedis;
/**
 * Created by songll on 2017/4/1.
 */
public class RedisSetJava {
    /**
     * 方法名：main</br>
     * 详述：Redis Java Set 实例</br>
     * 开发人员：souvc </br>
     * 创建时间：2015-12-10  </br>
     * @param args 说明返回值含义
     */
    public static void main(String[] args) {

        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接本地的 Redis 服务成功！");

        //删除map中的某个键值
        //jedis.hdel("user");

        //添加
        jedis.sadd("user","liuling");
        jedis.sadd("user","xinxin");
        jedis.sadd("user","ling");
        jedis.sadd("user","zhangxinxin");
        jedis.sadd("user","who");

        //移除noname
        jedis.srem("user","who");
        System.out.println(jedis.smembers("user"));//获取所有加入的value
        System.out.println(jedis.sismember("user", "who"));//判断 who 是否是user集合的元素
        System.out.println(jedis.srandmember("user"));
        System.out.println(jedis.scard("user"));//返回集合的元素个数

    }
}