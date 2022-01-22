package com.atguigu.gulimall.product.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class MyCacheConfig {
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig();
        conf = conf.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        conf = conf.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        CacheProperties.Redis redis = cacheProperties.getRedis();
        if (redis.getTimeToLive() != null) {
            conf = conf.entryTtl(redis.getTimeToLive());
        }
        if (redis.getKeyPrefix() != null) {
            conf = conf.prefixKeysWith(redis.getKeyPrefix());
        }
        if (!redis.isCacheNullValues()) {
            conf = conf.disableCachingNullValues();
        }
        if (!redis.isUseKeyPrefix()) {
            conf = conf.disableKeyPrefix();
        }
        return conf;
    }
}
