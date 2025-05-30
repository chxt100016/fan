package com.chxt;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Starter
 *
 * @author Frank Zhang
 */
@SpringBootApplication(scanBasePackages = {"com.chxt", "com.alibaba.cola"})
@MapperScan("com.chxt.db")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
