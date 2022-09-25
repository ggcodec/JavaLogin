package com.haotchen.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.haotchen.server")
public class JavaLoginModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaLoginModelApplication.class, args);
    }

}
