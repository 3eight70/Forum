package com.hits.file;

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
public class FileServer {
    public static void main(String[] args) {
        SpringApplication.run(FileServer.class, args);
    }
}