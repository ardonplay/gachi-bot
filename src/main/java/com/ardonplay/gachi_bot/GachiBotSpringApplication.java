package com.ardonplay.gachi_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableCaching
public class GachiBotSpringApplication {

  public static void main(String[] args) {
    SpringApplication.run(GachiBotSpringApplication.class, args);
  }

}
