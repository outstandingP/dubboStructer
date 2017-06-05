package com.outstanding.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Builder;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.SafeEncoder;

/**
 * The RedisUtil represents
 * @version $Id$
 * @author fengjc
 */
public class RedisUtil {

    /**
     * 数据源
     */
    private ShardedJedisPool shardedJedisPool;

    /** ======================================Strings====================================== */

    /**
     * Set the string value as value of the key. The string can't be longer than
     * 1073741824 bytes (1 GB).
     * Time complexity: O(1)
     * @param key
     * @param value
     * @return Status code reply
     */
    public String setString(String key, String value) {
        ShardedJedis jedis = this.shardedJedisPool.getResource();
        String status = jedis.set(key, value);
        this.shardedJedisPool.returnResource(jedis);
        return status;
    }

    /**
     * Get the value of the specified key. If the key does not exist the special
     * value 'nil' is returned. If the value stored at key is not a string an
     * error is returned because GET can only handle string values.
     * Time complexity: O(1)
     * @param key
     * @return Bulk reply
     */
    public String getString(String key) {
        ShardedJedis jedis = this.shardedJedisPool.getResource();
        String value = jedis.get(key);
        this.shardedJedisPool.returnResource(jedis);
        return value;
    }

    /**
     * This Stirng的批量更新
     * @param pairs
     */
    public List<Object> batchSetString(final List<Pair<String, String>> pairs) {
        final ShardedJedis jedis = this.shardedJedisPool.getResource();
        List<Object> status = jedis.pipelined(new ShardedJedisPipeline() {

            @Override
            public void execute() {
                for (Pair<String, String> pair : pairs) {
                    set(pair.getKey(), pair.getValue());
                }
            }
        });
        this.shardedJedisPool.returnResource(jedis);
        return status;
    }

    /**
     * This String的批量获得
     * @param keys
     * @return
     */
    public List<String> batchGetString(final List<String> keys) {
        final ShardedJedis jedis = this.shardedJedisPool.getResource();
        List<Object> dataList = jedis.pipelined(new ShardedJedisPipeline() {

            @Override
            public void execute() {
                for (String key : keys) {
                    get(key);
                }
            }
        });
        this.shardedJedisPool.returnResource(jedis);
        List<String> rtnDataList = new ArrayList<String>();
        for (Object data : dataList) {
            rtnDataList.add(STRING.build(data));
        }
        return rtnDataList;
    }

    /** ======================================Hashes====================================== */

    /**
     * Set the specified hash field to the specified value.
     * If key does not exist, a new key holding a hash is created.
     * Time complexity: O(1)
     * @param key
     * @param field
     * @param value
     * @return If the field already exists, and the HSET just produced an update
     *         of the value, 0 is returned, otherwise if a new field is created
     *         1 is returned.
     */
    public long hashSet(String key, String field, String value) {
        ShardedJedis jedis = this.shardedJedisPool.getResource();
        long count = jedis.hset(key, field, value);
        this.shardedJedisPool.returnResource(jedis);
        return count;
    }

    /**
     * If key holds a hash, retrieve the value associated to the specified
     * field.
     * If the field is not found or the key does not exist, a special 'nil'
     * value is returned.
     * Time complexity:O(1)
     * @param key
     * @param field
     * @return Bulk reply
     */
    public String hashGet(String key, String field) {
        ShardedJedis jedis = this.shardedJedisPool.getResource();
        String value = jedis.hget(key, field);
        this.shardedJedisPool.returnResource(jedis);
        return value;
    }

    /**
     * Set the respective fields to the respective values. HMSET replaces old
     * values with new values.
     * If key does not exist, a new key holding a hash is created.
     * Time complexity: O(N) (with N being the number of fields)
     * @param key
     * @param hash
     * @return Return OK or Exception if hash is empty
     */
    public String hashMultipleSet(String key, Map<String, String> hash) {
        ShardedJedis jedis = this.shardedJedisPool.getResource();
        String status = jedis.hmset(key, hash);
        this.shardedJedisPool.returnResource(jedis);
        return status;
    }

    /**
     * Retrieve the values associated to the specified fields.
     * If some of the specified fields do not exist, nil values are returned.
     * Non existing keys are considered like empty hashes.
     * Time complexity: O(N) (with N being the number of fields)
     * @param key
     * @param fields
     * @return Multi Bulk Reply specifically a list of all the values associated
     *         with the specified fields, in the same order of the request.
     */
    public List<String> hashMultipleGet(String key, String... fields) {
        ShardedJedis jedis = this.shardedJedisPool.getResource();
        List<String> dataList = jedis.hmget(key, fields);
        this.shardedJedisPool.returnResource(jedis);
        return dataList;
    }

    /**
     * This 批量的HashMultipleSet
     * @param pairs
     * @return
     */
    public List<Object> batchHashMultipleSet(final List<Pair<String, Map<String, String>>> pairs) {
        final ShardedJedis jedis = this.shardedJedisPool.getResource();
        List<Object> status = jedis.pipelined(new ShardedJedisPipeline() {

            @Override
            public void execute() {
                for (Pair<String, Map<String, String>> pair : pairs) {
                    hmset(pair.getKey(), pair.getValue());
                }
            }
        });
        this.shardedJedisPool.returnResource(jedis);
        return status;
    }

    /**
     * This 批量的HashMultipleGet
     * @param pairs
     * @return
     */
    public List<List<String>> batchHashMultipleGet(final List<Pair<String, String[]>> pairs) {
        final ShardedJedis jedis = this.shardedJedisPool.getResource();
        List<Object> dataList = jedis.pipelined(new ShardedJedisPipeline() {

            @Override
            public void execute() {
                for (Pair<String, String[]> pair : pairs) {
                    hmget(pair.getKey(), pair.getValue());
                }
            }
        });
        this.shardedJedisPool.returnResource(jedis);
        List<List<String>> rtnDataList = new ArrayList<List<String>>();
        for (Object data : dataList) {
            rtnDataList.add(STRING_LIST.build(data));
        }
        return rtnDataList;
    }

    /**
     * Return all the fields and associated values in a hash.
     * Time complexity: O(N), where N is the total number of entries
     * @param key
     * @return All the fields and values contained into a hash.
     */
    public Map<String, String> hashGetAll(String key) {
        ShardedJedis jedis = this.shardedJedisPool.getResource();
        Map<String, String> hash = jedis.hgetAll(key);
        this.shardedJedisPool.returnResource(jedis);
        return hash;
    }

    /**
     * This 批量的hashMultipleGet
     * @param keys
     * @return
     */
    public List<Map<String, String>> batchHashGetAll(final List<String> keys) {
        final ShardedJedis jedis = this.shardedJedisPool.getResource();
        List<Object> dataList = jedis.pipelined(new ShardedJedisPipeline() {

            @Override
            public void execute() {
                for (String key : keys) {
                    hgetAll(key);
                }
            }
        });
        this.shardedJedisPool.returnResource(jedis);
        List<Map<String, String>> rtnDataList = new ArrayList<Map<String, String>>();
        for (Object data : dataList) {
            rtnDataList.add(STRING_MAP.build(data));
        }
        return rtnDataList;
    }

    /** ======================================Builder====================================== */

    public static final Builder<Double> DOUBLE = new Builder<Double>() {

        @Override
        public Double build(Object data) {
            return Double.valueOf(STRING.build(data));
        }

        @Override
        public String toString() {
            return "double";
        }
    };

    public static final Builder<Boolean> BOOLEAN = new Builder<Boolean>() {

        @Override
        public Boolean build(Object data) {
            return ((Long) data) == 1;
        }

        @Override
        public String toString() {
            return "boolean";
        }
    };

    public static final Builder<Long> LONG = new Builder<Long>() {

        @Override
        public Long build(Object data) {
            return (Long) data;
        }

        @Override
        public String toString() {
            return "long";
        }

    };

    public static final Builder<String> STRING = new Builder<String>() {

        @Override
        public String build(Object data) {
            return SafeEncoder.encode((byte[]) data);
        }

        @Override
        public String toString() {
            return "string";
        }

    };

    public static final Builder<List<String>> STRING_LIST = new Builder<List<String>>() {

        @Override
        @SuppressWarnings("unchecked")
        public List<String> build(Object data) {
            if (null == data) {
                return null;
            }
            List<byte[]> l = (List<byte[]>) data;
            final ArrayList<String> result = new ArrayList<String>(l.size());
            for (final byte[] barray : l) {
                if (barray == null) {
                    result.add(null);
                } else {
                    result.add(SafeEncoder.encode(barray));
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return "List<String>";
        }

    };

    public static final Builder<Map<String, String>> STRING_MAP = new Builder<Map<String, String>>() {

        @Override
        @SuppressWarnings("unchecked")
        public Map<String, String> build(Object data) {
            final List<byte[]> flatHash = (List<byte[]>) data;
            final Map<String, String> hash = new HashMap<String, String>();
            final Iterator<byte[]> iterator = flatHash.iterator();
            while (iterator.hasNext()) {
                hash.put(SafeEncoder.encode(iterator.next()), SafeEncoder.encode(iterator.next()));
            }

            return hash;
        }

        @Override
        public String toString() {
            return "Map<String, String>";
        }

    };

    public static final Builder<Set<String>> STRING_SET = new Builder<Set<String>>() {

        @Override
        @SuppressWarnings("unchecked")
        public Set<String> build(Object data) {
            if (null == data) {
                return null;
            }
            List<byte[]> l = (List<byte[]>) data;
            final Set<String> result = new HashSet<String>(l.size());
            for (final byte[] barray : l) {
                if (barray == null) {
                    result.add(null);
                } else {
                    result.add(SafeEncoder.encode(barray));
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return "Set<String>";
        }

    };

    public static final Builder<Set<String>> STRING_ZSET = new Builder<Set<String>>() {

        @Override
        @SuppressWarnings("unchecked")
        public Set<String> build(Object data) {
            if (null == data) {
                return null;
            }
            List<byte[]> l = (List<byte[]>) data;
            final Set<String> result = new LinkedHashSet<String>(l.size());
            for (final byte[] barray : l) {
                if (barray == null) {
                    result.add(null);
                } else {
                    result.add(SafeEncoder.encode(barray));
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return "ZSet<String>";
        }

    };

    /** ======================================Other====================================== */

    public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
        this.shardedJedisPool = shardedJedisPool;
    }

    /**
     * This 构造Pair
     * @param key
     * @param value
     * @return
     */
    public <K, V> Pair<K, V> makePair(K key, V value) {
        return new Pair<K, V>(key, value);
    }

    /**
     * The Pair represents 键值对
     * @version $Id$
     * @author fengjc
     * @param <K>
     * @param <V>
     */
    public class Pair<K, V> {

        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return this.key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return this.value;
        }

        public void setValue(V value) {
            this.value = value;
        }

    }
}