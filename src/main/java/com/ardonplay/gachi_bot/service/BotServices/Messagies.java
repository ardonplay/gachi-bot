package com.ardonplay.gachi_bot.service.BotServices;

import com.ardonplay.gachi_bot.service.GachiBot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Messagies {

    final private GachiBot bot;

    final private DbController dbController;

    public Messagies(GachiBot bot) {
        this.bot = bot;
        this.dbController = new DbController(bot);
    }

    public void sendMessageWithReply(String text, Message message) {
        String chatId = message.getChatId().toString();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String text, Message message) {
        String chatId = message.getChatId().toString();
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendSticker(String stickerPath, Message message){
        String chatId = message.getChatId().toString();
        SendSticker sendSticker = new SendSticker(chatId, new InputFile(stickerPath));

        sendSticker.setReplyToMessageId(message.getMessageId());
        try {
            bot.execute(sendSticker);
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
            bot.execute(sendAnimation);
        } catch (TelegramApiException e) {
           e.printStackTrace();
        }
    }

    private int stringsLength(List<String> text) {
        int length = 0;
        for (String word : text) {
            length += word.length();
        }
        return length;
    }

    private List<String> stringLimiter(List<String> text) {
        List<String> parts = new ArrayList<>();
        int length = stringsLength(text);
        while (length > bot.getConfig().getMessageLength()) {
            final int mid = text.size() / 2;
            parts.add(text.subList(0, mid).toString());
            text = text.subList(mid, text.size());
            length = stringsLength(text);
        }
        parts.add(text.toString());
        return parts;
    }

    public void sendStat(Message message) {
        List<String> stat = bot.getUsers().get(message.getFrom().getId()).getMats().stream().map(mat -> mat.getWord() + "=" + mat.getCount()).toList();

        stringLimiter(stat).forEach(s -> sendMessageWithReply(s, message));
    }
}
