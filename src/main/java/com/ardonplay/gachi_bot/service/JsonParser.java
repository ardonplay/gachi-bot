package com.ardonplay.gachi_bot.service;

import com.ardonplay.gachi_bot.service.userTelegramModel.User;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Type;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component

public class JsonParser {

  @Getter
  @Setter
  private List<User> users;

  @Getter
  @Setter
  private Map<Long, User> persons = new HashMap<>();

  @Getter
  @Setter
  private List<String> badWords;

  static class BadWords {

    public List<String> bad_words;


    @Override
    public String toString() {
      return bad_words.toString();
    }
  }

  public BufferedReader getFile(String name) {
    InputStream inputStream = JsonParser.class.getClassLoader().getResourceAsStream(name);
    assert inputStream != null;
    InputStreamReader reader = new InputStreamReader(inputStream);

    return new BufferedReader(reader);
  }

  public void saveUsers() throws IOException {

    try (Writer writer = Files.newBufferedWriter(Path.of("src/main/resources/users.json"),
        StandardCharsets.UTF_8)) {

      Gson gson = new GsonBuilder().setPrettyPrinting().create();

      JsonElement tree = gson.toJsonTree(this.users);
      gson.toJson(tree, writer);
      System.out.println("json обновлен");
    }
  }

  public JsonParser() {

    Gson gson = new Gson();

    BufferedReader bufferedReader = getFile("users.json");

    Type type = new TypeToken<List<User>>() {
    }.getType();

    this.users = gson.fromJson(bufferedReader, type);

    bufferedReader = getFile("bad_words.json");

    type = new TypeToken<List<BadWords>>() {
    }.getType();

    List<BadWords> words = gson.fromJson(bufferedReader, type);

    for (BadWords word : words) {
      badWords = word.bad_words;
    }

    for (User person : this.users) {
      this.persons.put(person.user_id, person);
    }
  }
}

