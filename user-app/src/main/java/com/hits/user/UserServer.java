package com.hits.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD

@SpringBootApplication
=======
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
>>>>>>> 652e6b5cc00632fb43cd0fa859c1d48e64471d8d
public class UserServer {
    public static void main(String[] args) {
        SpringApplication.run(UserServer.class, args);
    }
}