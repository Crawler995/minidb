package com.bit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author aerfafish
 * @date 2020/9/19 1:05 下午
 */
@SpringBootApplication
@EnableScheduling
public class DBApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(DBApplication.class, args);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
