package com.product.filthilink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FilthiLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilthiLinkApplication.class, args);
    }

}
