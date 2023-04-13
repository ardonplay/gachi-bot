package com.ardonplay.gachi_bot.service.BotServices;

import com.ardonplay.gachi_bot.model.Mat;
import com.ardonplay.gachi_bot.model.User;
import com.ardonplay.gachi_bot.service.GachiBot;

import java.util.List;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class DbController {

  final private GachiBot bot;

  public DbController(GachiBot bot) {
    this.bot = bot;
  }

  public void saveUsers() {
    List<User> userList = bot.getUsers().values().stream().toList();

    userList.forEach(user -> {
      if (user.getMats() != null) {
        user.getMats().forEach(mat -> mat.setUser(user));
      }
    });

    if (!userList.equals(bot.getUserRepository().findAll())) {
      try {
        bot.getUserRepository().saveAll(userList);
      } catch (DataIntegrityViolationException e) {
        e.printStackTrace();
      }
    }
  }

  public void saveUser(User user) {
    if (user.getMats() != null) {
      user.getMats().forEach(mat -> mat.setUser(user));
    }
    try {
      bot.getUserRepository().save(user);
    } catch (DataIntegrityViolationException e) {
      e.printStackTrace();
    }

  }

}

