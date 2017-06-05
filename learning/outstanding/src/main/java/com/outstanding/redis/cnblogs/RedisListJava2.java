package com.outstanding.redis.cnblogs;

import redis.clients.jedis.Jedis;
/**
 * Created by songll on 2017/4/1.
 * http://www.cnblogs.com/liuhongfeng/p/5033559.html
 */
public class RedisListJava2 {
    /**
     * 方法名：main</br>
     * 详述：Redis Java List(列表) 实例  2 </br>
     * 开发人员：souvc </br>
     * 创建时间：2015-12-9  </br>
     * @param args 说明返回值含义
     * @throws
     */
    public static void main(String[] args) throws Exception{
        // 连接本地的 Redis 服务
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接本地的 Redis 服务成功！");

        //开始前，先移除所有的内容
        jedis.del("java framework");
        System.out.println(jedis.lrange("java framework",0,-1));

        //先向key java framework中存放三条数据
        jedis.lpush("java framework","spring");
        jedis.lpush("java framework","struts");
        jedis.lpush("java framework","hibernate");

        //再取出所有数据jedis.lrange是按范围取出，
        // 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有
        System.out.println(jedis.lrange("java framework",0,-1));

        jedis.del("java framework");
        jedis.rpush("java framework","spring");
        jedis.rpush("java framework","struts");
        jedis.rpush("java framework","hibernate");
        System.out.println(jedis.lrange("java framework",0,-1));
    }
}
