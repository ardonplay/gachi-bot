package com.ardonplay.gachi_bot.service.BotServices;

import com.ardonplay.gachi_bot.model.Mat;
import com.ardonplay.gachi_bot.model.User;
import com.ardonplay.gachi_bot.service.GachiBot;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class BotService {

  final private GachiBot bot;

  final private Messagies messagies;

  final private DbController dbController;

  public BotService(GachiBot bot) {
    this.bot = bot;
    this.messagies = new Messagies(bot);
    this.dbController = new DbController(bot);
  }

  public void sendMessageWithReply(String text, Message message) {
    messagies.sendMessageWithReply(text, message);
  }

  public void sendMessage(String text, Message message) {
    messagies.sendMessage(text, message);
  }

  public void sendDocument(String path, Message message) {
    messagies.sendDocument(path, message);
  }

  public void sendStat(Message message){
    messagies.sendStat(message);
  }
  public void sendSticker(String sticker, Message message){
    messagies.sendSticker(sticker, message);
  }

  private void counterSwitcher(Message message){
    switch (bot.getUsers().get(message.getFrom().getId()).getCounter()) {
      case 0, 1, 2 -> sendMessageWithReply( "🤡", message);
      case 3 -> {
        iterCounter(message.getFrom().getId());
        sendSticker(bot.getConfig().getStickers().get("billy"), message);
        sendMessage("За такие слова я тебя сейчас в бан кину", message);
      }
      default -> {
        changeCounter(message.getFrom().getId(), 0);
        restrictUser(message.getChatId().toString(), message.getFrom().getId());
        sendMessageWithReply("Ну ты дописался, посиди в бане минутку", message);
      }
    }
  }

  public void check_bad_word(String text, Message message) {
    int predCounter = bot.getUsers().get(message.getFrom().getId()).getCounter();
    for (String str : bot.getBad_words()) {
      if (text.contains(str)) {
        addUserStat(str, message);
        iterCounter(message.getFrom().getId());
      }
    }
    if(predCounter != bot.getUsers().get(message.getFrom().getId()).getCounter())
      counterSwitcher(message);
  }

  private void addUserStat(String word, Message message) {

    User user = bot.getUsers().get(message.getFrom().getId());
    try {
      if (user.getMats() != null) {
        Optional<Mat> mat = user.getMats().stream()
            .filter(mater -> Objects.equals(mater.getWord(), word)).findAny();

        mat.ifPresentOrElse(value -> value.setCount(value.getCount() + 1), () -> {
          Mat matershina = new Mat();
          matershina.setCount(1);
          matershina.setWord(word);
          user.getMats().add(matershina);
        });
      }
      else {
        user.setMats(new LinkedList<>());
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  public void changeCounter(long id, int counter) {
    bot.getUsers().get(id).setCounter(counter);
  }

  public void iterCounter(long id) {
    bot.getUsers().get(id).setCounter(bot.getUsers().get(id).getCounter() + 1);
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
      bot.execute(restrictChatMember);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  public void addUser(Message message) throws IOException {

    if (!bot.getUsers().containsKey(message.getFrom().getId())) {
      if (bot.getUserRepository().findById(message.getFrom().getId()).isEmpty()) {
        var userId = message.getFrom().getId();
        int counter = 0;
        com.ardonplay.gachi_bot.model.User user = new com.ardonplay.gachi_bot
            .model.User();
        user.setUserID(userId);
        user.setCounter(counter);

        bot.getUsers().put(userId, user);

        bot.getUserRepository().save(user);
      }
    }
  }

  public void saveUsers() throws IOException {
    dbController.saveUsers();
  }
}