package org.redisson.spring.session;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.session.config.EnableRedissonHttpSession;
import org.springframework.context.annotation.Bean;

@EnableRedissonHttpSession
public class Config {

    @Bean
    public RedissonClient redisson() {
        return Redisson.create();
    }
    
    @Bean
    public SessionEventsListener listener() {
        return new SessionEventsListener();
    }
    
}
