package com.outstanding.redis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.SortingParams;
/**
 * Created by songll on 2017/4/1.
 */
public class RedisClient {
    private Jedis jedis;//非切片额客户端连接
    private JedisPool jedisPool;//非切片连接池
    private ShardedJedis shardedJedis;//切片额客户端连接
    private ShardedJedisPool shardedJedisPool;//切片连接池

    public RedisClient()
    {
        initialPool();
        initialShardedPool();
        shardedJedis = shardedJedisPool.getResource();
        jedis = jedisPool.getResource();
    }

    /**
     * 初始化非切片池
     */
    private void initialPool()
    {
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig();
        //maxActive(jedis-2.1)  ==>  maxTotal(jedis-2.9)
        //maxWait(jedis-2.1)    ==>  maxWaitMillis(jedis-2.9)
        config.setMaxActive(20);
        config.setMaxWait(1000l);
        //config.setMaxTotal(20);
        //config.setMaxWaitMillis(10001);
        config.setMaxIdle(5);
        config.setTestOnBorrow(false);

        jedisPool = new JedisPool(config,"192.168.16.70",6379);
    }

    /**
     * 初始化切片池 
     */
    private void initialShardedPool()
    {
        // 池基本配置 
        JedisPoolConfig config = new JedisPoolConfig();
        //maxActive(jedis-2.1)  ==>  maxTotal(jedis-2.9)
        //maxWait(jedis-2.1)    ==>  maxWaitMillis(jedis-2.9)
        config.setMaxActive(20);
        config.setMaxWait(1000l);
        //config.setMaxTotal(20);
        //config.setMaxWaitMillis(10001);
        config.setMaxIdle(5);
        config.setTestOnBorrow(false);
        // slave链接 
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        shards.add(new JedisShardInfo("192.168.16.70", 6379, "master"));

        // 构造池 
        shardedJedisPool = new ShardedJedisPool(config, shards);
    }

    public void show() {
        KeyOperate();
        StringOperate();
        ListOperate();
        SetOperate();
        SortedSetOperate();
        HashOperate();
        jedisPool.returnResource(jedis);
        shardedJedisPool.returnResource(shardedJedis);
    }

    private void KeyOperate() {
         
    }

    private void StringOperate() {
         
    }

    private void ListOperate() {
    }

    private void SetOperate() {

    }

    private void SortedSetOperate() {

    }

    private void HashOperate() {

    }
}
