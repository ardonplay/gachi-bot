package com.ardonplay.gachi_bot.service;

import com.ardonplay.gachi_bot.config.BotConfig;
import com.ardonplay.gachi_bot.repository.BadWordRepository;
import com.ardonplay.gachi_bot.repository.MatRepository;
import com.ardonplay.gachi_bot.repository.UserRepository;
import com.ardonplay.gachi_bot.model.User;
import com.ardonplay.gachi_bot.repository.WhiteWordRepository;
import com.ardonplay.gachi_bot.service.BotServices.BotService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ardonplay.gachi_bot.service.BotServices.UserHandler;
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
    private final BadWordRepository badWordRepository;
    private final WhiteWordRepository whiteWordRepository;
    private final BotConfig config;
    private final BotService botService;


    @Autowired
    public GachiBot(UserRepository userRepository, MatRepository matRepository,
        BadWordRepository badWordRepository, WhiteWordRepository whiteWordRepository, BotConfig config) {
        super(config.getToken());
        this.userRepository = userRepository;
        this.matRepository = matRepository;
        this.badWordRepository = badWordRepository;
        this.whiteWordRepository = whiteWordRepository;
        this.config = config;
        this.botService = new BotService(this);
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();

            String text = message.getText().toLowerCase();
            try {
                botService.addUser(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Pattern pattern = Pattern.compile("/\\b[a-z]+");
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()){
                String match = matcher.group();
                switch (match){
                    case "/help" -> botService.sendMessageWithReply("Привет, я клоун!", message);
                    case "/stat" -> botService.sendStat(message);
                    case "/addwhiteword" -> {
                        try {
                            botService.addWhiteWord(message, text);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case "/addbadword" -> botService.addBadWord(message, text);
                    case "/rocket" -> botService.sendSticker(config.getStickers().get("rocket"), message);
                }
            }
            else {
                if (text.equals("@ardonplay_gachi_bot")) {
                    botService.sendMessageWithReply("Да жив я, жив!", message);
                } else {
                    botService.check_bad_word(text, message);
                }

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

