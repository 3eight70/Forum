package com.hits.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.hits.common.Entities")
public class UserServer {
    public static void main(String[] args) {
        SpringApplication.run(UserServer.class, args);
    }
}