package com.sen.senbackend;

import com.sen.senbackend.login.infrastructure.security.JwtConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties(value = JwtConfigProperties.class)
@EnableFeignClients
public class SenBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SenBackendApplication.class, args);
    }

}
