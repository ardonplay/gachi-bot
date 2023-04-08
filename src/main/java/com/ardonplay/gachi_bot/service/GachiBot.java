package com.ardonplay.gachi_bot.service;

import com.ardonplay.gachi_bot.config.BotConfig;
import com.ardonplay.gachi_bot.service.userTelegramModel.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;


import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class GachiBot extends TelegramLongPollingBot {

  final BotConfig config;
  Map<Long, User> users;

  List<String> bad_words;

  private final JsonParser jsonParser = new JsonParser();

  public GachiBot(BotConfig config) {
    super(config.getToken());
    this.config = config;

    this.users = jsonParser.getPersons();
    this.bad_words = jsonParser.getBadWords();

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        try {
          jsonParser.saveUsers();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };

    timer.schedule(task, 0L, 10*60 * 1000L);
  }

  public void sendMessage(String text, Message message) {
    String chatId = message.getChatId().toString();
    SendMessage sendMessage = new SendMessage();

    sendMessage.setChatId(chatId);
    sendMessage.setText(text);
    try {
      execute(sendMessage);
    } catch (TelegramApiException e) {
      System.out.println(e.getMessage());
    }
  }

  public void sendDocument(String path, Message message) {
    InputStream inputStream = getClass().getResourceAsStream(path);
    InputFile inputFile = new InputFile(inputStream, path);

    SendAnimation sendAnimation = new SendAnimation();

    sendAnimation.setChatId(message.getChatId());
    sendAnimation.setReplyToMessageId(message.getMessageId());

    sendAnimation.setAnimation(inputFile);

    try {
      execute(sendAnimation);
    } catch (TelegramApiException e) {
      System.out.println(e.getMessage());
    }

  }

  public void changeCounter(long id, int counter) {
    users.get(id).counter = counter;
  }

  public void iterCounter(long id) {
    users.get(id).counter += 1;
  }


  public void restrictUser(String chatId, long userId) {
    ChatPermissions chatPermissions = new ChatPermissions();
    chatPermissions.setCanSendMessages(false);
    chatPermissions.setCanSendPolls(false);
    chatPermissions.setCanSendOtherMessages(false);
    chatPermissions.setCanAddWebPagePreviews(false);

    RestrictChatMember restrictChatMember = new RestrictChatMember();
    restrictChatMember.setChatId(chatId);
    restrictChatMember.setUserId(userId);
    restrictChatMember.forTimePeriodDuration(Duration.ofMinutes(1));
    restrictChatMember.setPermissions(chatPermissions);

    try {
      execute(restrictChatMember);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  private void addUser(Message message) throws IOException {
    User user = new User();

    user.counter = 0;

    user.user_id = message.getFrom().getId();

    this.users.put(message.getFrom().getId(), user);
    jsonParser.saveUsers();
  }

  private void addUserStat(String word, Message message) {
    if (users.get(message.getFrom().getId()).stat.containsKey(word)) {
      User user = this.users.get(message.getFrom().getId());
      int count = user.stat.get(word);
      user.stat.put(word, count + 1);
    }
    else {
      User user = this.users.get(message.getFrom().getId());
      user.stat.put(word, 1);
    }
  }

  public void check_bad_word(String text, Message message) {
    for (String str : bad_words) {
      if (text.contains(str)) {
        addUserStat(str, message);
        iterCounter(message.getFrom().getId());
        switch (users.get(message.getFrom().getId()).counter) {
          case 0, 1, 2 -> sendMessageWithReply("🤡", message);
          case 3 -> {
            iterCounter(message.getFrom().getId());
            sendDocument("/gifs/sticker.gif", message);
            sendMessage("За такие слова я тебя сейчас в бан кину", message);
          }
          default -> {
            changeCounter(message.getFrom().getId(), 0);
            restrictUser(message.getChatId().toString(), message.getFrom().getId());
            sendMessageWithReply("Ну ты дописался, посиди в бане минутку", message);
          }
        }
      }
    }
  }

  public void sendStat(Message message) {

    sendMessageWithReply(users.get(message.getFrom().getId()).stat.toString(), message);
  }

  @Override
  public String getBotUsername() {
    return config.getName();
  }


  public void sendMessageWithReply(String text, Message message) {
    String chatId = message.getChatId().toString();
    SendMessage sendMessage = new SendMessage();

    sendMessage.setChatId(chatId);
    sendMessage.setReplyToMessageId(message.getMessageId());
    sendMessage.setText(text);
    try {
      execute(sendMessage);
    } catch (TelegramApiException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      Message message = update.getMessage();

      String text = message.getText();
      if (!users.containsKey(message.getFrom().getId())){
        try {
          addUser(message);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      switch (text) {
        case "/help" -> sendMessageWithReply("Привет, я клоун!", message);
        case "/stat", "/статистика" -> sendStat(message);
        case "@ardonplay_gachi_bot" -> sendMessageWithReply("Да жив я, жив!", message);

        default -> check_bad_word(text, message);
      }
    }
  }
}

