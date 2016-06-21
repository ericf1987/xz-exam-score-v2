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
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import javax.servlet.MultipartConfigElement;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Config {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${mongo.hosts}")
    private String mongoHosts;

    @Value("${scanner.db.addr}")
    private String scannerMongoAddr;

    @Value("${multipart.maxFileSize}")
    private String maxFileSize;

    @Value("${multipart.maxRequestSize}")
    private String maxRequestSize;

    @Bean
    public Redis redis() {
        return new Redis(redisHost, redisPort, 5);
    }

    private List<ServerAddress> readServerAddress(String serverAddress) {
        String[] split = serverAddress.split(",");
        List<ServerAddress> seeds = new ArrayList<>();

        for (String s : split) {
            if (s == null || s.length() == 0 || !s.contains(":")) {
                continue;
            }

            String[] host_port = s.split(":");
            seeds.add(new ServerAddress(host_port[0], Integer.parseInt(host_port[1])));
        }
        return seeds;
    }

    @Bean
    public MongoClient mongoClient() throws Exception {
        List<ServerAddress> seeds = readServerAddress(mongoHosts);
        MongoClientOptions options = MongoClientOptions.builder().build();  // 缺省连接池大小为100
        return new MongoClient(seeds, options);
    }

    @Bean
    public MongoClient scannerMongoClient() throws Exception {
        List<ServerAddress> seeds = readServerAddress(scannerMongoAddr);
        MongoClientOptions options = MongoClientOptions.builder().build();  // 缺省连接池大小为100
        return new MongoClient(seeds, options);
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

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(maxFileSize);
        factory.setMaxRequestSize(maxRequestSize);
        return factory.createMultipartConfig();
    }
}
