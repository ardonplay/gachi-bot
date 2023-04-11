package com.ardonplay.gachi_bot.service.userTelegramModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.swing.plaf.PanelUI;

@Component
@Data
public  class User {

  public long user_id;
  public int counter;
  public Map<String, Integer> stat = new HashMap<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return user_id == user.user_id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(user_id);
  }

  @Override
  public String toString() {
    return "User [user_id=" + user_id + ", counter=" + counter + ", stat= " + stat + "]";
  }
}
