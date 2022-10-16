package com.cs.core;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 */
@SpringBootApplication
@ComponentScan("com.cs")
@EnableDiscoveryClient
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class,args);
    }
}
