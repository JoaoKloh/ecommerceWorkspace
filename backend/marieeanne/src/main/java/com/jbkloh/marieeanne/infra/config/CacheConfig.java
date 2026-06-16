package com.jbkloh.marieeanne.infra.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(){
        SimpleCacheManager manager = new SimpleCacheManager();
        CaffeineCache marieCache = new CaffeineCache("mariecache", 
                Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(1000).build());
        CaffeineCache ipRateLimit = new CaffeineCache("ipRateLimit", 
                Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).maximumSize(10000).build());
        manager.setCaches(Arrays.asList(marieCache,ipRateLimit));
        return manager;
    }

    
}
