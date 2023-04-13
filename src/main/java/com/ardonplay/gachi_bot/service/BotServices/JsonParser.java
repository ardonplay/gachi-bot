package com.ardonplay.gachi_bot.service.BotServices;

import com.google.gson.reflect.TypeToken;

import java.io.*;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class JsonParser {

  private List<String> badWords;

  private List<String> whiteList;

  static class BadWords {

    public List<String> badWords;


    @Override
    public String toString() {
      return badWords.toString();
    }
  }
  static class WhiteWords {
    public List<String> whiteList;
  }

  public BufferedReader getFile(String name) {
    InputStream inputStream = JsonParser.class.getClassLoader().getResourceAsStream(name);
    assert inputStream != null;
    InputStreamReader reader = new InputStreamReader(inputStream);

    return new BufferedReader(reader);
  }

  public void saveWhiteWords(List<String> words) {
    List<WhiteWords> whiteWordsList = new ArrayList<>();
    WhiteWords whiteWords = new WhiteWords();
    whiteWords.whiteList = words;

    whiteWordsList.add(whiteWords);
    Gson gson = new Gson();
    String resourcesPath = JsonParser.class.getClassLoader().getResource("").getPath();
    String fileName = "white_list.json";
    String filePath = resourcesPath + "/" + fileName;
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(whiteWordsList, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public JsonParser() {

    Gson gson = new Gson();

    BufferedReader bufferedReader = getFile("bad_words.json");

    Type type = new TypeToken<List<BadWords>>() {
    }.getType();

    List<BadWords> words = gson.fromJson(bufferedReader, type);

    for (BadWords word : words) {
      badWords = word.badWords;
    }
    bufferedReader = getFile("white_list.json");
    type = new TypeToken<List<WhiteWords>>() {
    }.getType();

    List<WhiteWords> whiteWords = gson.fromJson(bufferedReader, type);
    for (WhiteWords word : whiteWords) {
      whiteList = word.whiteList;
    }
  }
}

