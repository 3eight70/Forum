package com.hits.forum;

import com.hits.security.Rest.Client.FileAppClient;
import com.hits.security.Rest.Client.UserAppClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {UserAppClient.class, FileAppClient.class})
@ComponentScan(basePackages = {"com.hits.forum", "com.hits.security"})
public class ForumServer {
    public static void main(String[] args) {
        SpringApplication.run(ForumServer.class, args);
    }
}