/**
 * Copyright 2016 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.mapreduce.RMapReduce;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.MapScanCodec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommand;
import org.redisson.client.protocol.RedisCommand.ValueType;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.client.protocol.convertor.BooleanReplayConvertor;
import org.redisson.client.protocol.convertor.NumberConvertor;
import org.redisson.client.protocol.decoder.MapScanResult;
import org.redisson.client.protocol.decoder.ScanObjectEntry;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.command.CommandExecutor;
import org.redisson.connection.decoder.MapGetAllDecoder;
import org.redisson.mapreduce.RedissonMapReduce;
import org.redisson.misc.Hash;

/**
 * Distributed and concurrent implementation of {@link java.util.concurrent.ConcurrentMap}
 * and {@link java.util.Map}
 *
 * @author Nikita Koksharov
 *
 * @param <K> key
 * @param <V> value
 */
public class RedissonMap<K, V> extends RedissonExpirable implements RMap<K, V> {

    static final RedisCommand<Object> EVAL_REMOVE = new RedisCommand<Object>("EVAL", 4, ValueType.MAP_KEY, ValueType.MAP_VALUE);
    static final RedisCommand<Object> EVAL_REPLACE = new RedisCommand<Object>("EVAL", 4, ValueType.MAP, ValueType.MAP_VALUE);
    static final RedisCommand<Boolean> EVAL_REPLACE_VALUE = new RedisCommand<Boolean>("EVAL", new BooleanReplayConvertor(), 4, Arrays.asList(ValueType.MAP_KEY, ValueType.MAP_VALUE, ValueType.MAP_VALUE));
    static final RedisCommand<Boolean> EVAL_REMOVE_VALUE = new RedisCommand<Boolean>("EVAL", new BooleanReplayConvertor(), 4, ValueType.MAP);
    static final RedisCommand<Object> EVAL_PUT = EVAL_REPLACE;

    private final UUID id;
    private final RedissonClient redisson;
    
    protected RedissonMap(UUID id, CommandAsyncExecutor commandExecutor, String name, RedissonClient redisson) {
        super(commandExecutor, name);
        this.id = id;
        this.redisson = redisson;
    }

    public RedissonMap(UUID id, Codec codec, CommandAsyncExecutor commandExecutor, String name, RedissonClient redisson) {
        super(codec, commandExecutor, name);
        this.id = id;
        this.redisson = redisson;
    }
    
    @Override
    public <KOut, VOut> RMapReduce<K, V, KOut, VOut> mapReduce() {
        return new RedissonMapReduce<K, V, KOut, VOut>(this, redisson, commandExecutor.getConnectionManager());
    }

    @Override
    public RLock getLock(K key) {
        String lockName = getLockName(key);
        return new RedissonLock((CommandExecutor)commandExecutor, lockName, id);
    }
    
    @Override
    public RReadWriteLock getReadWriteLock(K key) {
        String lockName = getLockName(key);
        return new RedissonReadWriteLock((CommandExecutor)commandExecutor, lockName, id);
    }
    
    private String getLockName(Object key) {
        try {
            byte[] keyState = codec.getMapKeyEncoder().encode(key);
            return suffixName(getName(), Hash.hashToBase64(keyState) + ":key");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public int size() {
        return get(sizeAsync());
    }

    @Override
    public RFuture<Integer> sizeAsync() {
        return commandExecutor.readAsync(getName(), codec, RedisCommands.HLEN, getName());
    }

    @Override
    public int valueSize(K key) {
        return get(valueSizeAsync(key));
    }
    
    @Override
    public RFuture<Integer> valueSizeAsync(K key) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        
        return commandExecutor.readAsync(getName(), codec, RedisCommands.HSTRLEN, getName(key), key);
    }
    
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(containsKeyAsync(key));
    }

    @Override
    public RFuture<Boolean> containsKeyAsync(Object key) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        
        return commandExecutor.readAsync(getName(key), codec, RedisCommands.HEXISTS, getName(key), key);
    }

    @Override
    public boolean containsValue(Object value) {
        return get(containsValueAsync(value));
    }

    @Override
    public RFuture<Boolean> containsValueAsync(Object value) {
        if (value == null) {
            throw new NullPointerException("map value can't be null");
        }
        
        return commandExecutor.evalReadAsync(getName(), codec, new RedisCommand<Boolean>("EVAL", new BooleanReplayConvertor(), 4),
                "local s = redis.call('hvals', KEYS[1]);" +
                        "for i = 1, #s, 1 do "
                            + "if ARGV[1] == s[i] then "
                                + "return 1 "
                            + "end "
                       + "end;" +
                     "return 0",
                Collections.<Object>singletonList(getName()), value);
    }

    @Override
    public Map<K, V> getAll(Set<K> keys) {
        return get(getAllAsync(keys));
    }

    @Override
    public RFuture<Map<K, V>> getAllAsync(Set<K> keys) {
        if (keys.size() == 0) {
            return newSucceededFuture(Collections.<K, V>emptyMap());
        }

        List<Object> args = new ArrayList<Object>(keys.size() + 1);
        args.add(getName());
        args.addAll(keys);
        return commandExecutor.readAsync(getName(), codec, new RedisCommand<Map<Object, Object>>("HMGET", new MapGetAllDecoder(args, 1), 2, ValueType.MAP_KEY, ValueType.MAP_VALUE), args.toArray());
    }

    @Override
    public V get(Object key) {
        return get(getAsync((K)key));
    }

    @Override
    public V put(K key, V value) {
        return get(putAsync(key, value));
    }

    @Override
    public V remove(Object key) {
        return get(removeAsync((K)key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        get(putAllAsync(map));
    }

    @Override
    public RFuture<Void> putAllAsync(Map<? extends K, ? extends V> map) {
        if (map.isEmpty()) {
            return newSucceededFuture(null);
        }

        List<Object> params = new ArrayList<Object>(map.size()*2 + 1);
        params.add(getName());
        for (java.util.Map.Entry<? extends K, ? extends V> t : map.entrySet()) {
            params.add(t.getKey());
            params.add(t.getValue());
        }

        return commandExecutor.writeAsync(getName(), codec, RedisCommands.HMSET, params.toArray());
    }

    @Override
    public void clear() {
        delete();
    }

    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public Collection<V> values() {
        return new Values();
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    @Override
    public Set<K> readAllKeySet() {
        return get(readAllKeySetAsync());
    }

    @Override
    public RFuture<Set<K>> readAllKeySetAsync() {
        return commandExecutor.readAsync(getName(), codec, RedisCommands.HKEYS, getName());
    }

    @Override
    public Collection<V> readAllValues() {
        return get(readAllValuesAsync());
    }

    @Override
    public RFuture<Collection<V>> readAllValuesAsync() {
        return commandExecutor.readAsync(getName(), codec, RedisCommands.HVALS, getName());
    }

    @Override
    public Set<Entry<K, V>> readAllEntrySet() {
        return get(readAllEntrySetAsync());
    }

    @Override
    public RFuture<Set<Entry<K, V>>> readAllEntrySetAsync() {
        return commandExecutor.readAsync(getName(), codec, RedisCommands.HGETALL_ENTRY, getName());
    }

    @Override
    public Map<K, V> readAllMap() {
        return get(readAllMapAsync());
    }

    @Override
    public RFuture<Map<K, V>> readAllMapAsync() {
        return commandExecutor.readAsync(getName(), codec, RedisCommands.HGETALL, getName());
    }

    
    @Override
    public V putIfAbsent(K key, V value) {
        return get(putIfAbsentAsync(key, value));
    }

    @Override
    public RFuture<V> putIfAbsentAsync(K key, V value) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        if (value == null) {
            throw new NullPointerException("map value can't be null");
        }
        
        return commandExecutor.evalWriteAsync(getName(key), codec, EVAL_PUT,
                 "if redis.call('hsetnx', KEYS[1], ARGV[1], ARGV[2]) == 1 then "
                    + "return nil "
                + "else "
                    + "return redis.call('hget', KEYS[1], ARGV[1]) "
                + "end",
                Collections.<Object>singletonList(getName(key)), key, value);
    }

    @Override
    public boolean fastPutIfAbsent(K key, V value) {
        return get(fastPutIfAbsentAsync(key, value));
    }

    @Override
    public RFuture<Boolean> fastPutIfAbsentAsync(K key, V value) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        if (value == null) {
            throw new NullPointerException("map value can't be null");
        }
        
        return commandExecutor.writeAsync(getName(key), codec, RedisCommands.HSETNX, getName(key), key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return get(removeAsync(key, value));
    }

    @Override
    public RFuture<Boolean> removeAsync(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        if (value == null) {
            throw new NullPointerException("map value can't be null");
        }
        
        return commandExecutor.evalWriteAsync(getName(key), codec, EVAL_REMOVE_VALUE,
                "if redis.call('hget', KEYS[1], ARGV[1]) == ARGV[2] then "
                        + "return redis.call('hdel', KEYS[1], ARGV[1]) "
                + "else "
                    + "return 0 "
                + "end",
            Collections.<Object>singletonList(getName(key)), key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return get(replaceAsync(key, oldValue, newValue));
    }

    @Override
    public RFuture<Boolean> replaceAsync(K key, V oldValue, V newValue) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        if (oldValue == null) {
            throw new NullPointerException("map oldValue can't be null");
        }
        if (newValue == null) {
            throw new NullPointerException("map newValue can't be null");
        }

        
        return commandExecutor.evalWriteAsync(getName(key), codec, EVAL_REPLACE_VALUE,
                "if redis.call('hget', KEYS[1], ARGV[1]) == ARGV[2] then "
                    + "redis.call('hset', KEYS[1], ARGV[1], ARGV[3]); "
                    + "return 1; "
                + "else "
                    + "return 0; "
                + "end",
                Collections.<Object>singletonList(getName(key)), key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return get(replaceAsync(key, value));
    }

    @Override
    public RFuture<V> replaceAsync(K key, V value) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        if (value == null) {
            throw new NullPointerException("map value can't be null");
        }
        
        return commandExecutor.evalWriteAsync(getName(key), codec, EVAL_REPLACE,
                "if redis.call('hexists', KEYS[1], ARGV[1]) == 1 then "
                    + "local v = redis.call('hget', KEYS[1], ARGV[1]); "
                    + "redis.call('hset', KEYS[1], ARGV[1], ARGV[2]); "
                    + "return v; "
                + "else "
                    + "return nil; "
                + "end",
            Collections.<Object>singletonList(getName(key)), key, value);
    }

    @Override
    public RFuture<V> getAsync(K key) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }

        return commandExecutor.readAsync(getName(key), codec, RedisCommands.HGET, getName(key), key);
    }
    
    @Override
    public RFuture<V> putAsync(K key, V value) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        if (value == null) {
            throw new NullPointerException("map value can't be null");
        }
        
        return commandExecutor.evalWriteAsync(getName(key), codec, EVAL_PUT,
                "local v = redis.call('hget', KEYS[1], ARGV[1]); "
                + "redis.call('hset', KEYS[1], ARGV[1], ARGV[2]); "
                + "return v",
                Collections.<Object>singletonList(getName(key)), key, value);
    }


    @Override
    public RFuture<V> removeAsync(K key) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }

        return commandExecutor.evalWriteAsync(getName(key), codec, EVAL_REMOVE,
                "local v = redis.call('hget', KEYS[1], ARGV[1]); "
                + "redis.call('hdel', KEYS[1], ARGV[1]); "
                + "return v",
                Collections.<Object>singletonList(getName(key)), key);
    }

    @Override
    public RFuture<Boolean> fastPutAsync(K key, V value) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        if (value == null) {
            throw new NullPointerException("map value can't be null");
        }
        
        return commandExecutor.writeAsync(getName(key), codec, RedisCommands.HSET, getName(key), key, value);
    }

    @Override
    public boolean fastPut(K key, V value) {
        return get(fastPutAsync(key, value));
    }

    @Override
    public RFuture<Long> fastRemoveAsync(K ... keys) {
        if (keys == null || keys.length == 0) {
            return newSucceededFuture(0L);
        }

        List<Object> args = new ArrayList<Object>(keys.length + 1);
        args.add(getName());
        args.addAll(Arrays.asList(keys));
        return commandExecutor.writeAsync(getName(), codec, RedisCommands.HDEL, args.toArray());
    }

    @Override
    public long fastRemove(K ... keys) {
        return get(fastRemoveAsync(keys));
    }

    MapScanResult<ScanObjectEntry, ScanObjectEntry> scanIterator(String name, InetSocketAddress client, long startPos) {
        RFuture<MapScanResult<ScanObjectEntry, ScanObjectEntry>> f 
            = commandExecutor.readAsync(client, name, new MapScanCodec(codec), RedisCommands.HSCAN, name, startPos);
        return get(f);
    }

    @Override
    public V addAndGet(K key, Number value) {
        return get(addAndGetAsync(key, value));
    }

    @Override
    public RFuture<V> addAndGetAsync(K key, Number value) {
        if (key == null) {
            throw new NullPointerException("map key can't be null");
        }
        if (value == null) {
            throw new NullPointerException("map value can't be null");
        }
        
        byte[] keyState = encodeMapKey(key);
        return commandExecutor.writeAsync(getName(key), StringCodec.INSTANCE,
                new RedisCommand<Object>("HINCRBYFLOAT", new NumberConvertor(value.getClass())),
                getName(key), keyState, new BigDecimal(value.toString()).toPlainString());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Map))
            return false;
        Map<?,?> m = (Map<?,?>) o;
        if (m.size() != size())
            return false;

        try {
            Iterator<Entry<K,V>> i = entrySet().iterator();
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(m.get(key)==null && m.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;
        Iterator<Entry<K,V>> i = entrySet().iterator();
        while (i.hasNext())
            h += i.next().hashCode();
        return h;
    }

    protected Iterator<K> keyIterator() {
        return new RedissonMapIterator<K, V, K>(RedissonMap.this) {
            @Override
            protected K getValue(java.util.Map.Entry<ScanObjectEntry, ScanObjectEntry> entry) {
                return (K) entry.getKey().getObj();
            }
        };
    }
    
    final class KeySet extends AbstractSet<K> {

        @Override
        public Iterator<K> iterator() {
            return keyIterator();
        }

        @Override
        public boolean contains(Object o) {
            return RedissonMap.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return RedissonMap.this.fastRemove((K)o) == 1;
        }

        @Override
        public int size() {
            return RedissonMap.this.size();
        }

        @Override
        public void clear() {
            RedissonMap.this.clear();
        }

    }

    protected Iterator<V> valueIterator() {
        return new RedissonMapIterator<K, V, V>(RedissonMap.this) {
            @Override
            protected V getValue(java.util.Map.Entry<ScanObjectEntry, ScanObjectEntry> entry) {
                return (V) entry.getValue().getObj();
            }
        };
    }

    final class Values extends AbstractCollection<V> {

        @Override
        public Iterator<V> iterator() {
            return valueIterator();
        }

        @Override
        public boolean contains(Object o) {
            return RedissonMap.this.containsValue(o);
        }

        @Override
        public int size() {
            return RedissonMap.this.size();
        }

        @Override
        public void clear() {
            RedissonMap.this.clear();
        }

    }

    protected Iterator<Map.Entry<K,V>> entryIterator() {
        return new RedissonMapIterator<K, V, Map.Entry<K, V>>(RedissonMap.this);
    }

    
    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {

        public final Iterator<Map.Entry<K,V>> iterator() {
            return entryIterator();
        }

        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>) o;
            Object key = e.getKey();
            V value = get(key);
            return value != null && value.equals(e);
        }

        public final boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>) o;
                Object key = e.getKey();
                Object value = e.getValue();
                return RedissonMap.this.remove(key, value);
            }
            return false;
        }

        public final int size() {
            return RedissonMap.this.size();
        }

        public final void clear() {
            RedissonMap.this.clear();
        }

    }

}
