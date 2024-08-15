package com.liboshuai.starlink.slr.connector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ConnectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConnectorApplication.class, args);
    }
}
