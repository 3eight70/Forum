package com.hits.user;

import com.hits.common.Client.ForumAppClient;
import com.hits.common.Client.UserAppClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {UserAppClient.class, ForumAppClient.class})
@ComponentScan(basePackages = {"com.hits.user", "com.hits.security"})
public class UserServer {
    public static void main(String[] args) {
        SpringApplication.run(UserServer.class, args);
    }
}