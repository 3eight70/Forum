package com.hits.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.hits.notification", "com.hits.security"})
public class NotificationServer {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServer.class, args);
    }
}