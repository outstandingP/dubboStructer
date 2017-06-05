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
package org.redisson.spring.cache;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * A {@link org.springframework.cache.CacheManager} implementation
 * backed by Redisson instance.
 *
 * @author Nikita Koksharov
 *
 */
public class RedissonSpringCacheManager implements CacheManager, ResourceLoaderAware, InitializingBean {

    private ResourceLoader resourceLoader;

    private Codec codec;

    private RedissonClient redisson;

    private Map<String, CacheConfig> configMap = new ConcurrentHashMap<String, CacheConfig>();
    private Map<String, Cache> instanceMap = new ConcurrentHashMap<String, Cache>();

    private String configLocation;

    /**
     * Creates CacheManager supplied by Redisson instance
     *
     * @param redisson object
     */
    public RedissonSpringCacheManager(RedissonClient redisson) {
        this(redisson, (String)null, null);
    }

    /**
     * Creates CacheManager supplied by Redisson instance and
     * Cache config mapped by Cache name
     *
     * @param redisson object
     * @param config object
     */
    public RedissonSpringCacheManager(RedissonClient redisson, Map<String, CacheConfig> config) {
        this(redisson, config, null);
    }

    /**
     * Creates CacheManager supplied by Redisson instance, Codec instance
     * and Cache config mapped by Cache name.
     * <p>
     * Each Cache instance share one Codec instance.
     *
     * @param redisson object
     * @param config object
     * @param codec object
     */
    public RedissonSpringCacheManager(RedissonClient redisson, Map<String, CacheConfig> config, Codec codec) {
        this.redisson = redisson;
        this.configMap = config;
        this.codec = codec;
    }

    /**
     * Creates CacheManager supplied by Redisson instance
     * and Cache config mapped by Cache name.
     * <p>
     * Loads the config file from the class path, interpreting plain paths as class path resource names
     * that include the package path (e.g. "mypackage/myresource.txt").
     *
     * @param redisson object
     * @param configLocation path
     */
    public RedissonSpringCacheManager(RedissonClient redisson, String configLocation) {
        this(redisson, configLocation, null);
    }

    /**
     * Creates CacheManager supplied by Redisson instance, Codec instance
     * and Config location path.
     * <p>
     * Each Cache instance share one Codec instance.
     * <p>
     * Loads the config file from the class path, interpreting plain paths as class path resource names
     * that include the package path (e.g. "mypackage/myresource.txt").
     *
     * @param redisson object
     * @param configLocation path
     * @param codec object
     */
    public RedissonSpringCacheManager(RedissonClient redisson, String configLocation, Codec codec) {
        this.redisson = redisson;
        this.configLocation = configLocation;
        this.codec = codec;
    }

    /**
     * Set cache config location
     *
     * @param configLocation object
     */
    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    /**
     * Set cache config mapped by cache name
     *
     * @param config object
     */
    public void setConfig(Map<String, CacheConfig> config) {
        this.configMap = config;
    }

    /**
     * Set Redisson instance
     *
     * @param redisson instance
     */
    public void setRedisson(RedissonClient redisson) {
        this.redisson = redisson;
    }

    /**
     * Set Codec instance shared between all Cache instances
     *
     * @param codec object
     */
    public void setCodec(Codec codec) {
        this.codec = codec;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = instanceMap.get(name);
        if (cache != null) {
            return cache;
        }
        
        CacheConfig config = configMap.get(name);
        if (config == null) {
            config = new CacheConfig();
            configMap.put(name, config);

            return createMap(name);
        }
        
        if (config.getMaxIdleTime() == 0 && config.getTTL() == 0) {
            return createMap(name);
        }
        
        return createMapCache(name, config);
    }

    private Cache createMap(String name) {
        RMap<Object, Object> map;
        if (codec != null) {
            map = redisson.getMap(name, codec);
        } else {
            map = redisson.getMap(name);
        }
        
        Cache cache = new RedissonCache(redisson, map);
        Cache oldCache = instanceMap.putIfAbsent(name, cache);
        if (oldCache != null) {
            cache = oldCache;
        }
        return cache;
    }

    private Cache createMapCache(String name, CacheConfig config) {
        RMapCache<Object, Object> map;
        if (codec != null) {
            map = redisson.getMapCache(name, codec);
        } else {
            map = redisson.getMapCache(name);
        }
        
        Cache cache = new RedissonCache(redisson, map, config);
        Cache oldCache = instanceMap.putIfAbsent(name, cache);
        if (oldCache != null) {
            cache = oldCache;
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(configMap.keySet());
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (configLocation == null) {
            return;
        }

        Resource resource = resourceLoader.getResource(configLocation);
        try {
            this.configMap = CacheConfig.fromJSON(resource.getInputStream());
        } catch (IOException e) {
            // try to read yaml
            try {
                this.configMap = CacheConfig.fromYAML(resource.getInputStream());
            } catch (IOException e1) {
                throw new BeanDefinitionStoreException(
                        "Could not parse cache configuration at [" + configLocation + "]", e1);
            }
        }
    }
    
}
