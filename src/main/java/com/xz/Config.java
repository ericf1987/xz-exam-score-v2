package com.xz;

import com.hyd.simplecache.EhCacheConfiguration;
import com.hyd.simplecache.SimpleCache;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.xz.ajiaedu.common.redis.Redis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Config {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${mongo.host}")
    private String mongoHost;

    @Value("${mongo.port}")
    private int mongoPort;

    @Bean
    public Redis redis() {
        return new Redis(redisHost, redisPort, 5);
    }

    @Bean
    public MongoClient mongoClient() throws Exception {
        ServerAddress serverAddress = new ServerAddress(mongoHost, mongoPort);
        MongoClientOptions options = MongoClientOptions.builder().build();  // 缺省连接池大小为100
        return new MongoClient(serverAddress, options);
    }

    @Bean
    public MongoDatabase scoreDatabase() throws Exception {
        return mongoClient().getDatabase("project_scores");
    }

    @Bean
    public SimpleCache simpleCache() {
        EhCacheConfiguration c = new EhCacheConfiguration();
        c.setTimeToLiveSeconds(600);
        c.setMaxEntriesLocalHeap(1000000);
        return new SimpleCache(c);
    }
}
