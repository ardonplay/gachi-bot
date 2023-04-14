package com.ardonplay.gachi_bot.service.BotServices;

import com.ardonplay.gachi_bot.model.BadWord;
import com.ardonplay.gachi_bot.model.User;
import com.ardonplay.gachi_bot.model.WhiteWord;
import com.ardonplay.gachi_bot.service.GachiBot;

import java.io.IOException;
import java.util.*;

import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class BotService {

    final private GachiBot bot;

    final private Messagies messagies;

    final private BadWordsHandler badWordsHandler;

    final private UserHandler userHandler;


    final private DbController dbController;

    public BotService(GachiBot bot) {
        this.bot = bot;
        this.messagies = new Messagies(bot);
        this.dbController = new DbController(bot);
        this.userHandler = new UserHandler(bot, dbController);
        this.badWordsHandler = new BadWordsHandler(bot, userHandler, dbController, messagies);
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

    public void sendStat(Message message) {
        messagies.sendStat(message);
    }

    public void sendSticker(String sticker, Message message) {
        messagies.sendSticker(sticker, message);
    }



    public void check_bad_word(String text, Message message) {
       badWordsHandler.check_bad_word(text, message);
    }

    private void addUserStat(String word, Message message) {
        userHandler.addUserStat(word, message);
    }

    public void changeCounter(long id, int counter) {
        badWordsHandler.changeCounter(id, counter);
    }

    public void iterCounter(long id) {
        badWordsHandler.iterCounter(id);
    }


    public void restrictUser(String chatId, long userId) {
        userHandler.restrictUser(chatId, userId);
    }

    public void addUser(Message message) throws IOException {
        userHandler.addUser(message);
    }

    public void saveUser(User user) {
        dbController.saveUser(user);
    }

    public void addWhiteWord(Message message, String text) throws IOException {
        List<String> words = Stream.of(text.split(" ")).filter(word -> word.toCharArray()[0] != '/' ).toList();
        for (String word : words) {
            if (!bot.getBadWordRepository().existsByWord(word) && !bot.getWhiteWordRepository()
                    .existsByWord(word)) {
                if(word.length() > 5) {
                    bot.getWhiteWordRepository().save(new WhiteWord(word));
                }
                else {
                    sendMessageWithReply("ты втираешь какую-то дичь", message);
                }
            } else {
                sendMessageWithReply("слово " + word + " уже в списке!", message);
            }
        }
    }

    public void addBadWord(Message message, String text) {
        List<String> words = Stream.of(text.split("\s")).toList();
        for (String word : words) {
            if (!bot.getBadWordRepository().existsByWord(word) && !bot.getWhiteWordRepository()
                    .existsByWord(word)) {
                if(word.length() > 5) {
                    bot.getBadWordRepository().save(new BadWord(word));
                }
                else {
                    sendMessageWithReply("ты втираешь какую-то дичь", message);
                }
            } else {
                sendMessageWithReply("слово " + word + " уже в списке!", message);
            }
        }
    }
}