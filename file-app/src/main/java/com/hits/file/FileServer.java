package com.hits.file;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.hits.common.Entities")
public class FileServer {
    public static void main(String[] args) {
        SpringApplication.run(FileServer.class, args);
    }
}