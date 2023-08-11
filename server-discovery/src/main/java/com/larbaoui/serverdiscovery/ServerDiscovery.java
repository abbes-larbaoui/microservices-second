package com.larbaoui.serverdiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServerDiscovery {
    public static void main(String[] args) {
        SpringApplication.run(ServerDiscovery.class, args);
    }
}
