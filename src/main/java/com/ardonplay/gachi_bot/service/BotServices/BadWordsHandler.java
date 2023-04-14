package com.ardonplay.gachi_bot.service.BotServices;

import com.ardonplay.gachi_bot.model.BadWord;
import com.ardonplay.gachi_bot.service.GachiBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class BadWordsHandler {

    final private GachiBot bot;

    final private UserHandler userHandler;

    final private DbController dbController;

    final private Messagies messagies;

    BadWordsHandler(GachiBot bot, UserHandler userHandler, DbController dbController, Messagies messagies) {
        this.bot = bot;
        this.userHandler = userHandler;
        this.dbController = dbController;
        this.messagies = messagies;
    }

    public void changeCounter(long id, int counter) {
        bot.getUserRepository().findById(id).ifPresent(
                user -> {
                    user.setCounter(counter);
                    dbController.saveUser(user);
                }
        );
    }

    public void iterCounter(long id) {
        bot.getUserRepository().findById(id).ifPresent(
                user -> {
                    user.setCounter(user.getCounter() + 1);
                    dbController.saveUser(user);
                }
        );
    }

    private void counterSwitcher(Message message) {
        bot.getUserRepository().findById(message.getFrom().getId()).ifPresent(
                user -> {
                    switch (user.getCounter()) {
                        case 0, 1, 2 -> messagies.sendMessageWithReply("🤡", message);
                        case 3 -> {
                            iterCounter(message.getFrom().getId());
                            messagies.sendSticker(bot.getConfig().getStickers().get("billy"), message);
                            messagies.sendMessage("За такие слова я тебя сейчас в бан кину", message);
                        }
                        default -> {
                            changeCounter(message.getFrom().getId(), 0);
                            userHandler.restrictUser(message.getChatId().toString(), message.getFrom().getId());
                            messagies.sendMessageWithReply("Ну ты дописался, посиди в бане минутку", message);
                        }
                    }
                }
        );
    }

    void check_bad_word(String text, Message message) {
        final int[] predCounter = {0};

        bot.getUserRepository().findById(message.getFrom().getId()).ifPresent(
                user -> predCounter[0] = user.getCounter()
        );

        List<String> words = List.of(text.split(" "));
        List<String> badWords = StreamSupport.stream(bot.getBadWordRepository()
                        .findAll()
                        .spliterator(), false)
                .map(BadWord::getWord).toList();
        for (String str : badWords) {
            for (String word : words) {
                if (word.contains(str) && !bot.getWhiteWordRepository().existsByWord(word)) {
                    iterCounter(message.getFrom().getId());
                    userHandler.addUserStat(str, message);
                }
            }
        }
        bot.getUserRepository().findById(message.getFrom().getId()).ifPresent(
                user -> {
                    if (predCounter[0] != user.getCounter()) {
                        counterSwitcher(message);
                    }
                }
        );
    }


}
