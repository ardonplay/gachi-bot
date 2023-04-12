package com.ardonplay.gachi_bot.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Data
@Configuration
@PropertySource("application.properties")
public class BotConfig {
  @Value("${bot.name}")
  String name;

  @Value("${bot.token}")
  String token;

  @Value("${bot.message_length}")
  int messageLength;

  @Value("#{${bot.stickers}}")
  private Map<String,String> stickers;

}
