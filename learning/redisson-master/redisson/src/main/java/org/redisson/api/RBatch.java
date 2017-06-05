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
package org.redisson.api;

import java.util.List;

import org.redisson.client.RedisException;
import org.redisson.client.codec.Codec;

/**
 * Interface for using pipeline feature.
 * <p>
 * All method invocations on objects
 * from this interface are batched to separate queue and could be executed later
 * with <code>execute()</code> or <code>executeAsync()</code> methods.
 * <p>
 * Please be ware, atomicity <b>is not</b> guaranteed.
 *
 *
 * @author Nikita Koksharov
 *
 */
public interface RBatch {

    /**
     * Returns geospatial items holder instance by <code>name</code>.
     * 
     * @param <V> type of object
     * @param name - name of object
     * @return Geo object
     */
    <V> RGeoAsync<V> getGeo(String name);

    /**
     * Returns geospatial items holder instance by <code>name</code>
     * using provided codec for geospatial members.
     *
     * @param <V> type of value
     * @param name - name of object
     * @param codec - codec for value
     * @return Geo object
     */
    <V> RGeoAsync<V> getGeo(String name, Codec codec);
    
    /**
     * Returns Set based MultiMap instance by name.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @return Multimap object
     */
    <K, V> RMultimapAsync<K, V> getSetMultimap(String name);

    /**
     * Returns Set based MultiMap instance by name
     * using provided codec for both map keys and values.
     * 
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @param codec - provided codec
     * @return Multimap object
     */
    <K, V> RMultimapAsync<K, V> getSetMultimap(String name, Codec codec);
    
    /**
     * Returns Set based Multimap instance by name.
     * Supports key-entry eviction with a given TTL value.
     * 
     * <p>If eviction is not required then it's better to use regular map {@link #getSetMultimap(String)}.</p>
     * 
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @return SetMultimapCache object
     */
    <K, V> RMultimapCacheAsync<K, V> getSetMultimapCache(String name);

    /**
     * Returns Set based Multimap instance by name
     * using provided codec for both map keys and values.
     * Supports key-entry eviction with a given TTL value.
     * 
     * <p>If eviction is not required then it's better to use regular map {@link #getSetMultimap(String, Codec)}.</p>
     * 
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @param codec - provided codec
     * @return SetMultimapCache object
     */
    <K, V> RMultimapCacheAsync<K, V> getSetMultimapCache(String name, Codec codec);
    
    /**
     * Returns set-based cache instance by <code>name</code>.
     * Uses map (value_hash, value) under the hood for minimal memory consumption.
     * Supports value eviction with a given TTL value.
     *
     * <p>If eviction is not required then it's better to use regular map {@link #getSet(String, Codec)}.</p>
     *
     * @param <V> type of value
     * @param name - name of object
     * @return SetCache object
     */
    <V> RSetCacheAsync<V> getSetCache(String name);

    /**
     * Returns set-based cache instance by <code>name</code>
     * using provided <code>codec</code> for values.
     * Uses map (value_hash, value) under the hood for minimal memory consumption.
     * Supports value eviction with a given TTL value.
     *
     * <p>If eviction is not required then it's better to use regular map {@link #getSet(String, Codec)}.</p>
     *
     * @param <V> type of value
     * @param name - name of object
     * @param codec - codec for values
     * @return SetCache object
     */
    <V> RSetCacheAsync<V> getSetCache(String name, Codec codec);

    /**
     * Returns map-based cache instance by <code>name</code>
     * using provided <code>codec</code> for both cache keys and values.
     * Supports entry eviction with a given TTL value.
     *
     * <p>If eviction is not required then it's better to use regular map {@link #getMap(String, Codec)}.</p>
     *
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @param codec - codec for keys and values
     * @return MapCache object
     */
    <K, V> RMapCacheAsync<K, V> getMapCache(String name, Codec codec);

    /**
     * Returns map-based cache instance by <code>name</code>.
     * Supports entry eviction with a given TTL value.
     *
     * <p>If eviction is not required then it's better to use regular map {@link #getMap(String)}.</p>
     *
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @return MapCache object
     */
    <K, V> RMapCacheAsync<K, V> getMapCache(String name);

    /**
     * Returns object holder by <code>name</code>
     *
     * @param <V> type of object
     * @param name - name of object
     * @return Bucket object
     */
    <V> RBucketAsync<V> getBucket(String name);

    <V> RBucketAsync<V> getBucket(String name, Codec codec);

    /**
     * Returns HyperLogLog object
     *
     * @param <V> type of object
     * @param name - name of object
     * @return HyperLogLog object
     */
    <V> RHyperLogLogAsync<V> getHyperLogLog(String name);

    <V> RHyperLogLogAsync<V> getHyperLogLog(String name, Codec codec);

    /**
     * Returns list instance by name.
     *
     * @param <V> type of object
     * @param name - name of object
     * @return List object
     */
    <V> RListAsync<V> getList(String name);

    <V> RListAsync<V> getList(String name, Codec codec);

    /**
     * Returns List based MultiMap instance by name.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @return ListMultimap object
     */
    <K, V> RMultimapAsync<K, V> getListMultimap(String name);

    /**
     * Returns List based MultiMap instance by name
     * using provided codec for both map keys and values.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @param codec - codec for keys and values
     * @return ListMultimap object
     */
    <K, V> RMultimapAsync<K, V> getListMultimap(String name, Codec codec);
    
    /**
     * Returns List based Multimap instance by name.
     * Supports key-entry eviction with a given TTL value.
     * 
     * <p>If eviction is not required then it's better to use regular map {@link #getSetMultimap(String)}.</p>
     * 
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @return ListMultimapCache object
     */
    <K, V> RMultimapAsync<K, V> getListMultimapCache(String name);
    
    /**
     * Returns List based Multimap instance by name
     * using provided codec for both map keys and values.
     * Supports key-entry eviction with a given TTL value.
     * 
     * <p>If eviction is not required then it's better to use regular map {@link #getSetMultimap(String, Codec)}.</p>
     * 
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @param codec - codec for keys and values
     * @return ListMultimapCache object
     */
    <K, V> RMultimapAsync<K, V> getListMultimapCache(String name, Codec codec);
    
    /**
     * Returns map instance by name.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @param name - name of object
     * @return Map object
     */
    <K, V> RMapAsync<K, V> getMap(String name);

    <K, V> RMapAsync<K, V> getMap(String name, Codec codec);

    /**
     * Returns set instance by name.
     *
     * @param <V> type of value
     * @param name - name of object
     * @return Set object
     */
    <V> RSetAsync<V> getSet(String name);

    <V> RSetAsync<V> getSet(String name, Codec codec);

    /**
     * Returns topic instance by name.
     *
     * @param <M> type of message
     * @param name - name of object
     * @return Topic object
     */
    <M> RTopicAsync<M> getTopic(String name);

    <M> RTopicAsync<M> getTopic(String name, Codec codec);

    /**
     * Returns queue instance by name.
     *
     * @param <V> type of value
     * @param name - name of object
     * @return Queue object
     */
    <V> RQueueAsync<V> getQueue(String name);

    <V> RQueueAsync<V> getQueue(String name, Codec codec);

    /**
     * Returns blocking queue instance by name.
     *
     * @param <V> type of value
     * @param name - name of object
     * @return BlockingQueue object
     */
    <V> RBlockingQueueAsync<V> getBlockingQueue(String name);

    <V> RBlockingQueueAsync<V> getBlockingQueue(String name, Codec codec);

    /**
     * Returns deque instance by name.
     *
     * @param <V> type of value
     * @param name - name of object
     * @return Deque object
     */
    <V> RDequeAsync<V> getDeque(String name);

    <V> RDequeAsync<V> getDeque(String name, Codec codec);

    /**
     * Returns blocking deque instance by name.
     * 
     * @param <V> type of value
     * @param name - name of object
     * @return BlockingDeque object
     */
    <V> RBlockingDequeAsync<V> getBlockingDeque(String name);

    <V> RBlockingDequeAsync<V> getBlockingDeque(String name, Codec codec);

    /**
     * Returns atomicLong instance by name.
     *
     * @param name - name of object
     * @return AtomicLong object
     */
    RAtomicLongAsync getAtomicLong(String name);

    /**
     * Returns atomicDouble instance by name.
     *
     * @param name - name of object
     * @return AtomicDouble object
     */
    RAtomicDoubleAsync getAtomicDouble(String name);

    /**
     * Returns Redis Sorted Set instance by name
     * 
     * @param <V> type of value
     * @param name - name of object
     * @return ScoredSortedSet object
     */
    <V> RScoredSortedSetAsync<V> getScoredSortedSet(String name);

    <V> RScoredSortedSetAsync<V> getScoredSortedSet(String name, Codec codec);

    /**
     * Returns String based Redis Sorted Set instance by name
     * All elements are inserted with the same score during addition,
     * in order to force lexicographical ordering
     *
     * @param name - name of object
     * @return LexSortedSet object
     */
    RLexSortedSetAsync getLexSortedSet(String name);

    RBitSetAsync getBitSet(String name);

    /**
     * Returns script operations object
     *
     * @return Script object
     */
    RScriptAsync getScript();

    /**
     * Returns keys operations.
     * Each of Redis/Redisson object associated with own key
     *
     * @return Keys object
     */
    RKeysAsync getKeys();

    /**
     * Executes all operations accumulated during async methods invocations.
     * <p>
     * If cluster configuration used then operations are grouped by slot ids
     * and may be executed on different servers. Thus command execution order could be changed
     *
     * @return List with result object for each command
     * @throws RedisException in case of any error
     *
     */
    List<?> execute() throws RedisException;

    /**
     * Executes all operations accumulated during async methods invocations asynchronously.
     * <p>
     * In cluster configurations operations grouped by slot ids
     * so may be executed on different servers. Thus command execution order could be changed
     *
     * @return List with result object for each command
     */
    RFuture<List<?>> executeAsync();

    /**
     * Executes all operations accumulated during async methods invocations. 
     * Command replies are skipped such approach saves response bandwidth.
     * <p>
     * If cluster configuration used then operations are grouped by slot ids
     * and may be executed on different servers. Thus command execution order could be changed.
     * <p>
     * NOTE: Redis 3.2+ required
     *
     * @throws RedisException in case of any error
     *
     */
    void executeSkipResult();

    /**
     * Executes all operations accumulated during async methods invocations asynchronously, 
     * Command replies are skipped such approach saves response bandwidth.
     * <p>
     * If cluster configuration used then operations are grouped by slot ids
     * and may be executed on different servers. Thus command execution order could be changed
     * <p>
     * NOTE: Redis 3.2+ required
     * 
     * @return void
     * @throws RedisException in case of any error
     *
     */
    RFuture<Void> executeSkipResultAsync();
}
