package com.bsg.anagrams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AnagramsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnagramsApplication.class, args);
    }
}
