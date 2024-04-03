package com.hits.file;

import com.hits.security.Client.ForumAppClient;
import com.hits.security.Client.UserAppClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {UserAppClient.class, ForumAppClient.class})
@ComponentScan(basePackages = {"com.hits.file", "com.hits.security"})
public class FileServer {
    public static void main(String[] args) {
        SpringApplication.run(FileServer.class, args);
    }
}