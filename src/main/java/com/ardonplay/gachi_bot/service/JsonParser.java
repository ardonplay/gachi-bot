package com.ardonplay.gachi_bot.service;

import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import java.util.List;

import com.google.gson.Gson;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class JsonParser {

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

  public JsonParser() {

    Gson gson = new Gson();

    BufferedReader bufferedReader = getFile("bad_words.json");

    Type type = new TypeToken<List<BadWords>>() {
    }.getType();

    List<BadWords> words = gson.fromJson(bufferedReader, type);

    for (BadWords word : words) {
      badWords = word.bad_words;
    }
  }
}

