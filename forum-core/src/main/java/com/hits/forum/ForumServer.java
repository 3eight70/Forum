package com.hits.forum;

import com.hits.user.Configurations.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@Import(SecurityConfig.class)
@ComponentScan(basePackages = {"com.hits.forum", "com.hits.user"})
public class ForumServer {
    public static void main(String[] args) {
        SpringApplication.run(ForumServer.class, args);
    }
}