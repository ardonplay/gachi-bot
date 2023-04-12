package com.ardonplay.gachi_bot.service;

import com.ardonplay.gachi_bot.config.BotConfig;
import com.ardonplay.gachi_bot.repository.MatRepository;
import com.ardonplay.gachi_bot.repository.UserRepository;
import com.ardonplay.gachi_bot.model.User;
import com.ardonplay.gachi_bot.service.BotServices.BotService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;


import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.Message;


@Component
@Getter
@Setter
public class GachiBot extends TelegramLongPollingBot {

    private final UserRepository userRepository;
    private final MatRepository matRepository;
    private final BotConfig config;
    private final BotService botService;

    private Map<Long, User> users;
    private List<String> bad_words;


    @Autowired
    public GachiBot(UserRepository userRepository, MatRepository matRepository, BotConfig config) {
        super(config.getToken());
        this.userRepository = userRepository;
        this.matRepository = matRepository;
        this.config = config;
        this.botService = new BotService(this);
        this.users = new LinkedHashMap<>();
        for (User user : userRepository.findAll()) {
            this.users.put(user.getUserID(), user);
        }
        JsonParser jsonParser = new JsonParser();
        this.bad_words = jsonParser.getBadWords();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    botService.saveUsers();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        timer.schedule(task, 0L, 300000L);
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();

            String text = message.getText();
            try {
                botService.addUser(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            switch (text) {
                case "/help" -> botService.sendMessageWithReply("Привет, я клоун!", message);
                case "/stat", "/статистика" -> botService.sendStat(message);
                case "@ardonplay_gachi_bot" -> botService.sendMessageWithReply("Да жив я, жив!", message);
                case "/rocket" -> botService.sendSticker(config.getStickers().get("rocket"), message);
                default -> botService.check_bad_word(text, message);
            }
        } else if (update.hasMessage() && update.getMessage().getNewChatMembers() != null) {
            Message message = update.getMessage();

            try {
                Long botID = getMeAsync().get().getId();
                for (var user : message.getNewChatMembers()) {
                    if (user.getId().equals(botID)) {
                        botService.sendMessage("Ну привет мои маленькие slaves," +
                                " зовите меня своим dungeon masterом, " +
                                "я вам не дам повода материться," +
                                " а если вы будете это делать - придется " +
                                "сделать fisting ass...", message);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }


        }
    }
}

