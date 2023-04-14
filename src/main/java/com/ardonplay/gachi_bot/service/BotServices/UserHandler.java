package com.ardonplay.gachi_bot.service.BotServices;

import com.ardonplay.gachi_bot.model.Mat;
import com.ardonplay.gachi_bot.model.User;
import com.ardonplay.gachi_bot.service.GachiBot;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.time.Duration;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

@Component
public class UserHandler {
    private final GachiBot bot;

    private final DbController dbController;

    UserHandler(GachiBot bot, DbController dbController) {
        this.bot = bot;
        this.dbController = dbController;
    }

    void restrictUser(String chatId, long userId) {
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

    void addUserStat(String word, Message message) {

        Optional<User> optionalUser = bot.getUserRepository().findById(message.getFrom().getId());
        optionalUser.ifPresent(user -> {
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
                    bot.getBotService().saveUser(user);
                } else {
                    user.setMats(new LinkedList<>());
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
    }

    void addUser(Message message) {

        if (bot.getUserRepository().existsById(message.getFrom().getId())) {
            var userId = message.getFrom().getId();
            int counter = 0;
            User user = new User();
            user.setUserID(userId);
            user.setCounter(counter);

            dbController.saveUser(user);
        }
    }
}
