package com.sf.cloud.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import com.sf.cloud.core.configuration.AxonConfig;

@SpringBootApplication
@EnableDiscoveryClient
@Import({AxonConfig.class})
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class);
    }
}
