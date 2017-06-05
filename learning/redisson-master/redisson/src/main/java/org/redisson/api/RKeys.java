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

import java.util.Collection;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public interface RKeys extends RKeysAsync {

    /**
     * Update the last access time of an object. 
     * 
     * @param names of keys
     * @return count of objects were touched
     */
    long touch(String... names);
    
    /**
     * Checks if provided keys exist
     * 
     * @param names of keys
     * @return amount of existing keys
     */
    long countExists(String... names);
    
    /**
     * Get Redis object type by key
     * 
     * @param key - name of key
     * @return type of key
     */
    RType getType(String key);
    
    /**
     * Get hash slot identifier for key.
     * Available for cluster nodes only
     *
     * @param key - name of key
     * @return slot number
     */
    int getSlot(String key);

    /**
     * Get all keys by pattern using iterator. 
     * Keys traversed with SCAN operation. Each SCAN operation loads 
     * up to <b>10</b> keys per request. 
     * <p>
     *  Supported glob-style patterns:
     *  <p>
     *    h?llo subscribes to hello, hallo and hxllo
     *    <p>
     *    h*llo subscribes to hllo and heeeello
     *    <p>
     *    h[ae]llo subscribes to hello and hallo, but not hillo
     * 
     * @param pattern - match pattern
     * @return Iterable object
     */
    Iterable<String> getKeysByPattern(String pattern);

    /**
     * Get all keys by pattern using iterator. 
     * Keys traversed with SCAN operation. Each SCAN operation loads 
     * up to <code>count</code> keys per request. 
     * <p>
     *  Supported glob-style patterns:
     *  <p>
     *    h?llo subscribes to hello, hallo and hxllo
     *    <p>
     *    h*llo subscribes to hllo and heeeello
     *    <p>
     *    h[ae]llo subscribes to hello and hallo, but not hillo
     *
     * @param pattern - match pattern
     * @param count - keys loaded per request to Redis
     * @return Iterable object
     */
    Iterable<String> getKeysByPattern(String pattern, int count);
    
    /**
     * Get all keys using iterator. Keys traversing with SCAN operation
     *
     * @return Iterable object
     */
    Iterable<String> getKeys();

    /**
     * Get random key
     *
     * @return random key
     */
    String randomKey();

    /**
     * Find keys by key search pattern
     *
     *  Supported glob-style patterns:
     *    h?llo subscribes to hello, hallo and hxllo
     *    h*llo subscribes to hllo and heeeello
     *    h[ae]llo subscribes to hello and hallo, but not hillo
     *
     * @param pattern - match pattern
     * @return collection of keys
     */
    Collection<String> findKeysByPattern(String pattern);

    /**
     * Delete multiple objects by a key pattern.
     * <p>
     * Method executes in <b>NON atomic way</b> in cluster mode due to lua script limitations.
     * <p>
     *  Supported glob-style patterns:
     *    h?llo subscribes to hello, hallo and hxllo
     *    h*llo subscribes to hllo and heeeello
     *    h[ae]llo subscribes to hello and hallo, but not hillo
     *
     * @param pattern - match pattern 
     * @return number of removed keys
     */
    long deleteByPattern(String pattern);

    /**
     * Delete multiple objects
     *
     * @param objects of Redisson
     * @return number of removed keys
     */
    long delete(RObject ... objects);
    
    /**
     * Delete multiple objects by name
     *
     * @param keys - object names
     * @return number of removed keys
     */
    long delete(String ... keys);

    /**
     * Returns the number of keys in the currently-selected database
     *
     * @return count of keys
     */
    long count();

    /**
     * Delete all keys of currently selected database
     */
    void flushdb();

    /**
     * Delete all keys of all existing databases
     */
    void flushall();

}
