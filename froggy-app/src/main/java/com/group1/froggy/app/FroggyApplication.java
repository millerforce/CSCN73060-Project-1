package com.group1.froggy.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.group1.froggy.*")
@ComponentScan(basePackages = "com.group1.froggy.*")
@EnableJpaRepositories(basePackages = "com.group1.froggy.*")
@SpringBootApplication
public class FroggyApplication {

    public static void main(String[] args) {
        SpringApplication.run(FroggyApplication.class, args);
    }
}
