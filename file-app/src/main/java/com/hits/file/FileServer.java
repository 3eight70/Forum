package com.hits.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FileServer {
    public static void main(String[] args) {
        SpringApplication.run(FileServer.class, args);
    }
}